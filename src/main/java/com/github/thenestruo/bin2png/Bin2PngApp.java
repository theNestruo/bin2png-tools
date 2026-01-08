package com.github.thenestruo.bin2png;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.zip.CRC32;

import javax.imageio.ImageIO;

import org.tinylog.Logger;

import com.github.thenestruo.bin2png.impl.AbstractVisualizer;
import com.github.thenestruo.bin2png.impl.CharsetHorizontalVisualizer;
import com.github.thenestruo.bin2png.impl.ColoredGroupedVerticalVisualizer;
import com.github.thenestruo.bin2png.impl.GroupedVerticalVisualizer;
import com.github.thenestruo.bin2png.impl.HighlightVerticalVisualizer;
import com.github.thenestruo.bin2png.impl.HorizontalVisualizer;
import com.github.thenestruo.bin2png.impl.MsxColorVisualizer;
import com.github.thenestruo.bin2png.impl.MsxMonochromeVisualizer;
import com.github.thenestruo.bin2png.impl.MsxSpritesVisualizer;
import com.github.thenestruo.bin2png.impl.SpritesVerticalVisualizer;
import com.github.thenestruo.bin2png.impl.VerticalVisualizer;
import com.github.thenestruo.bin2png.impl.ZxColorVisualizer;
import com.github.thenestruo.bin2png.impl.ZxMonochromeVisualizer;
import com.github.thenestruo.commons.Bools;
import com.github.thenestruo.commons.io.Paths;

import picocli.CommandLine;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "bin2png", sortOptions = false)
public class Bin2PngApp implements Callable<Integer> {

	public static void main(final String... args) {
		System.exit(new CommandLine(new Bin2PngApp()).execute(args));
	}

	@Option(names = { "-h", "--help" }, usageHelp = true, description = "shows usage")
	private boolean help;

	@Parameters(index = "0", arity = "1", paramLabel = "input", description = "binary input file")
	private Path inputPath;

	@Parameters(index = "1",
			arity = "0..1",
			paramLabel = "output",
			description = "PNG output file (optional, defaults to <input>.png)")
	private Path outputPath;

	@Option(names = { "-start" }, description = "Start offset, in bytes (default: 0)", defaultValue = "0")
	private Integer start;

	@Option(names = { "-width" }, description = "Width, in pixels (for ZX-ordered graphics visualizer)")
	private Integer width;

	@Option(names = { "-height" }, description = "Height, in pixels (for ZX-ordered graphics visualizer)")
	private Integer height;

	@Option(names = { "-images" }, description = "Number of consecutive images (default: 1)", defaultValue = "1")
	private Integer imageCount;

	@Option(names = { "-spacing" }, description = "Spacing, in pixels (default: 2)", defaultValue = "2")
	private Integer spacing;

	@ArgGroup
	private Mode mode;

	static class Mode {

		@Option(names = { "--horizontal" }, description = "Uses the horizontal visualizer")
		private boolean isHorizontal;

		@Option(names = { "-l", "--highlight" }, description = "Uses the padding/ASCII/CALLs/JPs highlight visualizer")
		private boolean isHighlight;

		@Option(names = { "-s", "--sprites" }, description = "Uses the 16x16 sprites visualizer")
		private boolean isSprites;

		@Option(names = { "-c", "--charset" }, description = "Uses the charset graphics visualizer")
		private boolean isCharset;

		@Option(names = { "-zx" }, description = "Uses the monochrome ZX-ordered graphics visualizer")
		private boolean isZxMonochrome;

		@Option(names = { "-zxcolor" },
				description = "Uses the ZX-ordered graphics visualizer, followed by CLRTBL data")
		private boolean isZxColor;

		@Option(names = { "-vgroup" }, description = "Uses the grouped vertical visualizer")
		private boolean isVGroupMonochrome;

		@Option(names = { "-vgroupcolor" },
				description = "Uses the grouped vertical visualizer, followed by CLRTBL data")
		private boolean isVGroupColor;

		@Option(names = { "-msx" }, description = "Uses the monochrome MSX-ordered graphics visualizer")
		private boolean isMsxMonochrome;

		@Option(names = { "-msxcolor" },
				description = "Uses the MSX-ordered graphics visualizer, followed by CLRTBL data")
		private boolean isMsxColor;

		@Option(names = { "-msxsprites" }, description = "Uses the MSX-ordered 16x16 sprites visualizer")
		private boolean isMsxSprites;

		@Option(names = { "-f", "-biosfont" },
				description = "Checks the file is an MSX BIOS image and extracts the font")
		private boolean isBiosFont;
	}

	@Override
	public Integer call() throws Exception {

		// Reads the binary file
		final byte[] bytes = this.readBinary(this.inputPath);
		if (bytes == null) {
			return 10;
		}
		Logger.debug("Binary file read: {} bytes", bytes.length);

		// Reads the parameters
		final AbstractVisualizer visualizer = this.buildVisualizer();

		// Generates the image
		final BufferedImage image;
		final Path outputPath;
		if (this.mode.isBiosFont) {

			// Reads the data buffer
			final byte[] buffer = Files.readAllBytes(this.inputPath);

			// Validates it looks like an MSX BIOS
			Bools.requireTrue(buffer.length == 0x8000);
			final int cgtablAddress = Byte.toUnsignedInt(buffer[0x0004])
					+ (Byte.toUnsignedInt(buffer[0x0005]) * 0x0100);
			Bools.requireTrue(cgtablAddress <= (0x8000 - 0x0800));
			for (int i = 0; i < 8; i++) {
				Bools.requireTrue(buffer[cgtablAddress + i] == (byte) 0x00);
				Bools.requireTrue(buffer[cgtablAddress + (0x20 * 8) + i] == (byte) 0x00);
			}

			final byte[] cgtabl = Arrays.copyOfRange(buffer, cgtablAddress, cgtablAddress + 0x0800);
			final CRC32 crc32 = new CRC32();
			crc32.reset();
			crc32.update(cgtabl);

			image = visualizer.renderImage(cgtabl);
			final Long biosFontCrc32 = crc32.getValue();
			outputPath = this.outputPath(String.format(".%08X.png", biosFontCrc32));

		} else if (this.start > 0) {

			// Validates start offset and length
			Bools.requireTrue(bytes.length > this.start);

			final byte[] subarray = Arrays.copyOfRange(bytes, this.start, bytes.length);

			image = visualizer.renderImage(subarray);
			outputPath = this.outputPath(String.format(".%04X.png", this.start));

		} else {
			image = visualizer.renderImage(Files.readAllBytes(this.inputPath));
			outputPath = this.outputPath(".png");
		}

		// Writes the PNG file
		Logger.debug("{}x{} image will be written to PNG file {}", image.getWidth(), image.getHeight(), outputPath);
		this.writePngFile(outputPath, image);
		Logger.debug("PNG file {} written", outputPath);

		return 0;
	}

	private byte[] readBinary(final Path path) throws IOException {

		// (sanity check)
		if (path == null) {
			return null;
		}

		if (!Files.exists(path)) {
			Logger.warn("Binary input file {} does not exist", path);
			return null;
		}

		Logger.debug("Binary input file {} will be read", path);
		return Files.readAllBytes(path);
	}

	private AbstractVisualizer buildVisualizer() {
		if (this.mode != null) {
			if (this.mode.isHighlight) {
				return new HighlightVerticalVisualizer(this.height, this.spacing);
			}
			if (this.mode.isSprites) {
				return new SpritesVerticalVisualizer(this.height, this.spacing);
			}
			if (this.mode.isCharset) {
				return new CharsetHorizontalVisualizer(this.width, this.spacing);
			}
			if (this.mode.isHorizontal) {
				return new HorizontalVisualizer(this.width, this.spacing);
			}
			if (this.mode.isBiosFont) {
				return new HorizontalVisualizer(0);
			}
			if (this.mode.isZxMonochrome) {
				return new ZxMonochromeVisualizer(this.width, this.height, this.imageCount, this.spacing);
			}
			if (this.mode.isZxColor) {
				return new ZxColorVisualizer(this.width, this.height, this.imageCount, this.spacing);
			}
			if (this.mode.isVGroupMonochrome) {
				return new GroupedVerticalVisualizer(this.width, this.height, this.imageCount, this.spacing);
			}
			if (this.mode.isVGroupColor) {
				return new ColoredGroupedVerticalVisualizer(this.width, this.height, this.imageCount, this.spacing);
			}
			if (this.mode.isMsxMonochrome) {
				return new MsxMonochromeVisualizer(this.imageCount, this.spacing);
			}
			if (this.mode.isMsxColor) {
				return new MsxColorVisualizer(this.imageCount, this.spacing);
			}
			if (this.mode.isMsxSprites) {
				return new MsxSpritesVisualizer(this.imageCount, this.spacing);
			}
		}
		return new VerticalVisualizer(this.height, this.spacing);
	}

	private Path outputPath(final String suffix) {

		return this.outputPath != null ? this.outputPath : Paths.append(this.inputPath, suffix);
	}

	private void writePngFile(final Path path, final BufferedImage image) throws IOException {

		ImageIO.write(image, "PNG", path.toFile());
	}
}
