package com.github.thenestruo.bin2png;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.CRC32;

import javax.imageio.ImageIO;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.thenestruo.util.FileSystemResource;
import com.github.thenestruo.util.ReadableResource;

public class Bin2PngApp {

	private static final String HELP = "help";

	private static final String START = "start";
	private static final String WIDTH = "width";
	private static final String HEIGHT = "height";
	private static final String IMAGES = "images";
	private static final String SPACING = "spacing";

	private static final String HORIZONTAL = "horizontal";
	private static final String HIGHLIGHT = "highlight";
	private static final String SPRITES = "sprites";
	private static final String CHARSET = "charset";
	private static final String ZX = "zx";
	private static final String ZXCOLOR = "zxcolor";
	private static final String VGROUP = "vgroup";
	private static final String VGROUPCOLOR = "vgroupcolor";

	private static final String BIOSFONT = "biosfont";

	private static final Logger logger = LoggerFactory.getLogger(Bin2PngApp.class);

	public static void main(final String[] args) throws ParseException, IOException {

		// Parses the command line
		final Options options = options();
		final CommandLine command;
		try {
			command = new DefaultParser().parse(options, args);
		} catch (final MissingOptionException e) {
			showUsage(options);
			return;
		}

		// Main options
		if (showUsage(command, options)) {
			return;
		}
		// setVerbose(command);

		// Reads the binary file
		final Pair<String, ReadableResource> pair = readBinary(command);
		final String inputFilePath = pair.getLeft();
		final ReadableResource inputFile = pair.getRight();
		if (inputFile == null) {
			return;
		}
		logger.debug("Binary file read: {} bytes", inputFile.sizeOf());

		// Reads the parameters
		final Integer width = command.hasOption(WIDTH)
				? Integer.parseUnsignedInt(command.getOptionValue(WIDTH))
				: null;
		final Integer height = command.hasOption(HEIGHT)
				? Integer.parseUnsignedInt(command.getOptionValue(HEIGHT))
				: null;
		final int imageCount = Integer.parseUnsignedInt(command.getOptionValue(IMAGES, Integer.toString(1)));
		final int spacing = Integer.parseUnsignedInt(command.getOptionValue(SPACING, Integer.toString(2)));

		final AbstractVisualizer visualizer =
				command.hasOption(HIGHLIGHT) ? new HighlightVerticalVisualizer(height, spacing)
				: command.hasOption(SPRITES) ? new SpritesVerticalVisualizer(height, spacing)
				: command.hasOption(CHARSET) ? new CharsetHorizontalVisualizer(width, spacing)
				: command.hasOption(HORIZONTAL) ? new HorizontalVisualizer(width, spacing)
				: command.hasOption(BIOSFONT) ? new HorizontalVisualizer(0)
				: command.hasOption(ZX) ? new ZxMonochromeVisualizer(width, height, imageCount, spacing)
				: command.hasOption(ZXCOLOR) ? new ZxColorVisualizer(width, height, imageCount, spacing)
				: command.hasOption(VGROUP) ? new GroupedVerticalVisualizer(width, height, imageCount, spacing)
				: command.hasOption(VGROUPCOLOR) ? new ColoredGroupedVerticalVisualizer(width, height, imageCount, spacing)
				: new VerticalVisualizer(height, spacing);

		// Generates the image
		final BufferedImage image;
		final String pngFilePath;
		if (command.hasOption(BIOSFONT)) {

			// Reads the data buffer
			final byte[] buffer;
			try (final InputStream is = IOUtils.buffer(inputFile.getInputStream())) {
				buffer = IOUtils.toByteArray(is);
			}

			// Validates it looks like an MSX BIOS
			Validate.isTrue(buffer.length == 0x8000);
			final int cgtablAddress = Byte.toUnsignedInt(buffer[0x0004]) + (Byte.toUnsignedInt(buffer[0x0005]) * 0x0100);
			Validate.isTrue(cgtablAddress <= (0x8000 - 0x0800));
			for (int i = 0; i < 8; i++) {
				Validate.isTrue(buffer[cgtablAddress + i] == (byte) 0x00);
				Validate.isTrue(buffer[cgtablAddress + (0x20 * 8) + i] == (byte) 0x00);
			}

			final byte[] cgtabl = ArrayUtils.subarray(buffer, cgtablAddress, cgtablAddress + 0x0800);
			final CRC32 crc32 = new CRC32();
			crc32.reset();
			crc32.update(cgtabl);

			image = visualizer.renderImage(cgtabl);
			final Long biosFontCrc32 = crc32.getValue();
			pngFilePath = nextPath(command, String.format("%08X.%s.png", biosFontCrc32, inputFilePath));

		} else if (command.hasOption(START)) {

			// Reads the data buffer
			final byte[] buffer;
			try (final InputStream is = IOUtils.buffer(inputFile.getInputStream())) {
				buffer = IOUtils.toByteArray(is);
			}

			// Validates start offset and length
			final int startOffset = Integer.decode(command.getOptionValue(START, Integer.toString(0)));
			Validate.isTrue(buffer.length > startOffset);

			final byte[] subarray = ArrayUtils.subarray(buffer, startOffset, buffer.length);

			image = visualizer.renderImage(subarray);
			pngFilePath = nextPath(command, String.format("%s.%04X.png", inputFilePath, startOffset));

		} else {
			image = visualizer.renderImage(inputFile);
			pngFilePath = nextPath(command, String.format("%s.png", inputFilePath));
	}

		// Writes the PNG file
		logger.debug("{}x{} image will be written to PNG file {}", image.getWidth(), image.getHeight(), pngFilePath);
		writePngFile(pngFilePath, image);
		logger.debug("PNG file {} written", pngFilePath);
	}

	private static Options options() {

		final Options options = new Options();
		options.addOption(HELP, "Shows usage");
		// options.addOption(SIZE, true, "Size, in pixels (default: 256)");
		options.addOption(START, true, "Start offset, in bytes (default: 0)");
		options.addOption(WIDTH, true, "Width, in pixels (for ZX-ordered graphics visualizer)");
		options.addOption(HEIGHT, true, "Height, in pixels (for ZX-ordered graphics visualizer)");
		options.addOption(IMAGES, true, "Number of consecutive images (default: 1)");
		options.addOption(SPACING, true, "Spacing, in pixels (default: 2)");
		options.addOption("h", HORIZONTAL, false, "Uses the horizontal visualizer");
		options.addOption("l", HIGHLIGHT, false, "Uses the padding/ASCII/CALLs/JPs highlight visualizer");
		options.addOption("s", SPRITES, false, "Uses the 16x16 sprites visualizer");
		options.addOption("c", CHARSET, false, "Uses the charset graphics visualizer");
		options.addOption(ZX, false, "Uses the monochrome ZX-ordered graphics visualizer");
		options.addOption(ZXCOLOR, false, "Uses the ZX-ordered graphics visualizer, followed by CLRTBL data");
		options.addOption(VGROUP, false, "Uses the grouped vertical visualizer");
		options.addOption(VGROUPCOLOR, false, "Uses the grouped vertical visualizer, followed by CLRTBL data");
		options.addOption("f", BIOSFONT, false, "Checks the file is an MSX BIOS image and extracts the font");
		return options;
	}

	private static boolean showUsage(final CommandLine command, final Options options) {

		return command.hasOption(HELP)
				? showUsage(options)
				: false;
	}

	private static boolean showUsage(final Options options) {

		// (prints in proper order)
		final HelpFormatter helpFormatter = new HelpFormatter();
		final PrintWriter pw = new PrintWriter(new OutputStreamWriter(System.out, StandardCharsets.ISO_8859_1));
		helpFormatter.printUsage(pw, 114, "java -jar bin2png.jar");
		for (final Option option : options.getOptions()) {
			helpFormatter.printOptions(pw, 114, new Options().addOption(option), 2, 4);
		}
		helpFormatter.printWrapped(pw, 114, "  <input>    Binary input file");
		helpFormatter.printWrapped(pw, 114, "  <output>   PNG output file (optional, defaults to <input>.png)");
		pw.flush();

		return true;
	}

	private static Pair<String, ReadableResource> readBinary(final CommandLine command) throws IOException {

		final String path = nextPath(command, null);
		if (path == null) {
			return Pair.of(null, null);
		}
		final File file = new File(path);
		if (!file.exists()) {
			logger.warn("Binary input file {} does not exist", file.getAbsolutePath());
			return Pair.of(path, null);
		}

		logger.debug("Binary input file {} will be read", file.getAbsolutePath());
		return Pair.of(path, new FileSystemResource(file));
	}

	private static void writePngFile(final String path, final BufferedImage image) throws IOException {

		final File file = new File(path);
		// if (file.exists()) {
		// 	logger.warn("PNG output file {} already exists", file.getAbsolutePath());
		// 	return;
		// }

		ImageIO.write(image, "PNG", file);
	}

	private static String nextPath(final CommandLine command, final String defaultValue) {

		final List<String> argList = command.getArgList();
		return argList.isEmpty() ? defaultValue : argList.remove(0);
	}
}
