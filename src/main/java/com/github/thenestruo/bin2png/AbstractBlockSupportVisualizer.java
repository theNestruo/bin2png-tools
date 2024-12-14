package com.github.thenestruo.bin2png;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

import org.apache.commons.lang3.tuple.Pair;

public abstract class AbstractBlockSupportVisualizer extends AbstractLineSupportVisualizer {

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
}
