package com.github.thenestruo.bin2png;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;

public class ZxMonochromeVisualizer extends AbstractLineSupportVisualizer {

	protected final int width;
	protected final int height;

	public ZxMonochromeVisualizer(final int width, final int height) {

		Validate.isTrue((width > 0) && ((width % 8) == 0), "Width %d is not a mutiple of 8", width);
		Validate.isTrue(height > 0, "Height %d is not a positive number", height);

		this.width = width;
		this.height = height;
	}

	@Override
	public BufferedImage renderImage(byte[] buffer) throws IOException {

		Objects.requireNonNull(buffer);

		// Reads the data buffer
		final int size = buffer.length;

		// Creates the canvas
		final BufferedImage image = new BufferedImage(this.width, this.height, BufferedImage.TYPE_3BYTE_BGR);

		for (int address = 0; address < size; address++) {
			this.renderLine(buffer, image, address);
		}

		return image;
	}

	private void renderLine(byte[] buffer, BufferedImage image, int address) {

		final int value = this.valueAt(buffer, address);
		final Pair<Integer, Integer> location = this.locationFor(address);
		final int x = location.getLeft();
		final int y = location.getRight();
		final Pair<Integer, Integer> colors = this.colorsFor(buffer, address);
		this.doRenderLine(image, value, x, y, colors);
	}

	@Override
	protected Pair<Integer, Integer> colorsFor(byte[] buffer, int address) {

		return DEFAULT_COLORS;
	}

	protected Pair<Integer, Integer> locationFor(final int address) {

		final int x = Math.min((address * 8) % this.width, this.width -1);
		final int y = Math.min((address * 8) / this.width, this.height -1);
		return Pair.of(x, y);
	}
}
