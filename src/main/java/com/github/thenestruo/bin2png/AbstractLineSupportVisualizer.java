package com.github.thenestruo.bin2png;

import java.awt.image.BufferedImage;

import org.apache.commons.lang3.tuple.Pair;

public abstract class AbstractLineSupportVisualizer extends AbstractVisualizer {

	protected static final Pair<Integer, Integer> DEFAULT_COLORS = Pair.of(0xd3d4d2, 0x252526);

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
