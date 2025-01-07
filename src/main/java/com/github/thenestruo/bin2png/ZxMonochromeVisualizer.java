package com.github.thenestruo.bin2png;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;

public class ZxMonochromeVisualizer extends AbstractLineSupportVisualizer {

	protected final int width;
	protected final int height;
	protected final int imageCount;
	protected final int spacing;

	protected final int imageSize;
	protected final int totalImageSize;

	public ZxMonochromeVisualizer(final Integer width, final Integer height, final int imageCount, final int spacing) {

		Objects.requireNonNull(width);
		Objects.requireNonNull(height);
		Validate.isTrue((width > 0) && ((width % 8) == 0), "Width %d is not a mutiple of 8", width);
		Validate.isTrue(height > 0, "Height %d is not a positive number", height);

		this.width = width;
		this.height = height;
		this.imageCount = imageCount;
		this.spacing = spacing;

		this.imageSize = width * height / 8;
		this.totalImageSize = this.imageSize * imageCount;
	}

	@Override
	public BufferedImage renderImage(final byte[] buffer) throws IOException {

		Objects.requireNonNull(buffer);

		// Reads the data buffer
		final int size = buffer.length;

		// Creates the canvas
		final int totalWidth = (this.width * this.imageCount) + (this.spacing * (this.imageCount - 1));
		final BufferedImage image = new BufferedImage(totalWidth, this.height, BufferedImage.TYPE_3BYTE_BGR);

		for (int address = 0; address < Math.min(size, this.totalImageSize); address++) {
			this.renderLine(buffer, image, address);
		}

		return image;
	}

	protected void renderLine(final byte[] buffer, final BufferedImage image, final int address) {

		if (address >= this.totalImageSize) {
			// (ignored)
			return;
		}

		final int value = this.valueAt(buffer, address);
		final Pair<Integer, Integer> location = this.locationFor(address);
		final int x = location.getLeft();
		final int y = location.getRight();
		final Pair<Integer, Integer> colors = this.colorsFor(buffer, address);

		this.doRenderLine(image, value, x, y, colors);

	}

	@Override
	protected Pair<Integer, Integer> colorsFor(final byte[] buffer, final int address) {

		return DEFAULT_COLORS;
	}

	protected Pair<Integer, Integer> locationFor(final int address) {

		final int image = address / this.imageSize;
		final int imageX = ((this.width + this.spacing) * image);

		final int x = (((address % this.imageSize) * 8) % this.width) + imageX;
		final int y = (((address % this.imageSize) * 8) / this.width);

		return Pair.of(x, y);
	}
}
