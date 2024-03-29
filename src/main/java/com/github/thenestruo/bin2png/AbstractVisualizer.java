package com.github.thenestruo.bin2png;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;

import com.github.thenestruo.util.ReadableResource;

public abstract class AbstractVisualizer {

	protected static final Pair<Integer, Integer> DEFAULT_COLORS = Pair.of(0xd3d4d2, 0x252526);

	public final BufferedImage renderImage(final ReadableResource resource) throws IOException {

		Objects.requireNonNull(resource);

		// Reads the data buffer
		final byte[] buffer;
		try (final InputStream is = IOUtils.buffer(resource.getInputStream())) {
			buffer = IOUtils.toByteArray(is);
		}
		Validate.isTrue(buffer.length == (int) resource.sizeOf());

		return this.renderImage(buffer);
	}

	public final BufferedImage renderImage(final byte[] buffer) throws IOException {

		Objects.requireNonNull(buffer);

		// Reads the data buffer
		final int size = buffer.length;

		// Creates the canvas
		final BufferedImage image = new BufferedImage(
				this.computeImageWidth(size),
				this.computeImageHeight(size),
				BufferedImage.TYPE_3BYTE_BGR);

		for (int address = 0; address < size; address += 8) {
			this.renderBlock(buffer, image, address);
		}

		return image;
	}

	protected abstract int computeImageWidth(int size);

	protected abstract int computeImageHeight(int size);

	protected void renderBlock(final byte[] buffer, final BufferedImage image, final int address) {

		final Pair<Integer, Integer> location = this.locationFor(address);
		final int x = location.getLeft();
		final int y = location.getRight();
		this.doRenderBlock(buffer, image, address, x, y, null);
	}

	protected abstract Pair<Integer, Integer> locationFor(int address);

	protected final void doRenderBlock(
			final byte[] buffer, final BufferedImage bufferedImage, final int pAddress, final int x, final int pY, final Pair<Integer, Integer> colorsForced) {

		for (int y = pY, address = pAddress; y < (pY + 8); y++, address++) {
			final int value = this.valueAt(buffer, address);
			final Pair<Integer, Integer> colors =
					colorsForced != null ? colorsForced : this.colorsFor(buffer, address);
			this.doRenderLine(bufferedImage, value, x, y, colors);
		}
	}

	protected int valueAt(final byte[] buffer, final int address) {

		return (address >= 0) && (address < buffer.length)
				? Byte.toUnsignedInt(buffer[address])
				: 0x00;
	}

	protected Pair<Integer, Integer> colorsFor(final byte[] buffer, final int address) {

		return this.colorsFor(this.valueAt(buffer, address));
	}

	protected Pair<Integer, Integer> colorsFor(final int value) {

		return DEFAULT_COLORS;
	}

	protected final void doRenderLine(
			final BufferedImage bufferedImage, final int value, final int pX, final int y, final Pair<Integer, Integer> colors) {

		final Integer color1 = colors.getLeft();
		final Integer color0 = colors.getRight();
		for (int x = pX + 7, bit = 0, shiftedValue = value; bit < 8; x--, bit++, shiftedValue >>= 1) {
			final int color = ((shiftedValue & 1) != 0) ? color1 : color0;
			bufferedImage.setRGB(x, y, color);
		}
	}
}
