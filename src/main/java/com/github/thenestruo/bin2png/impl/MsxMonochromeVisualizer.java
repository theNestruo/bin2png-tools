package com.github.thenestruo.bin2png.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

import com.github.thenestruo.commons.maps.Pair;

public class MsxMonochromeVisualizer extends ZxMonochromeVisualizer {

	public MsxMonochromeVisualizer(final int imageCount, final int spacing) {
		this(8, 8, imageCount, spacing);
	}

	protected MsxMonochromeVisualizer(final Integer width, final Integer height, final int imageCount,
			final int spacing) {
		super(width, height, imageCount, spacing);
	}

	@Override
	public BufferedImage renderImage(final byte[] buffer) throws IOException {

		Objects.requireNonNull(buffer);

		// Reads the data buffer
		final int size = buffer.length;

		// Creates the canvas
		final int columns = Math.min(this.imageCount, 16);
		final int rows = ((this.imageCount - 1) / 16) + 1;
		final int totalWidth = (this.width * columns) + (this.spacing * (columns - 1));
		final int totalHeight = (this.height * rows) + (this.spacing * (rows - 1));
		final BufferedImage image = new BufferedImage(totalWidth, totalHeight, BufferedImage.TYPE_3BYTE_BGR);

		for (int address = 0; address < Math.min(size, this.totalImageSize); address++) {
			this.renderLine(buffer, image, address);
		}

		return image;
	}

	@Override
	protected Pair<Integer, Integer> locationFor(final int address) {

		final int image = address / this.imageSize;
		final int imageX = ((this.width + this.spacing) * (image % 16));
		final int imageY = ((this.height + this.spacing) * (image / 16));

		final int x = (((address % this.imageSize) / this.height) * 8) + imageX;
		final int y = ((address % this.imageSize) % this.height) + imageY;

		return Pair.of(x, y);
	}
}
