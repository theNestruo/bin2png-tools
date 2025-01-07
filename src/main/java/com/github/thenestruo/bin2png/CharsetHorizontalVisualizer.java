package com.github.thenestruo.bin2png;

import java.awt.image.BufferedImage;

import org.apache.commons.lang3.tuple.Pair;

public class CharsetHorizontalVisualizer extends HorizontalVisualizer {

	protected static final Pair<Integer, Integer> SHADOW_COLORS = Pair.of(0x838482, 0x1e1e1e);

	public CharsetHorizontalVisualizer(
			final Integer targetWidth,
			final int vSpacing) {
		super(targetWidth, vSpacing);
	}

	@Override
	protected int computeImageHeight(final int size) {

		final int rows = ((size - 1) / this.targetWidth) + 1;
		return (rows * 16) + ((rows - 1) * this.vSpacing);
	}

	@Override
	protected Pair<Integer, Integer> locationFor(final int address) {

		final int row = address / this.targetWidth;
		final int x = (address & 0xfff8) % this.targetWidth;
		final int y = row * (16 + this.vSpacing);
		return Pair.of(x, y);
	}

	@Override
	protected void renderBlock(final byte[] buffer, final BufferedImage image, final int address) {

		final Pair<Integer, Integer> location = this.locationFor(address);
		final int x = location.getLeft();
		final int y = location.getRight();
		this.doRenderBlock(buffer, image, address,    x, y,    DEFAULT_COLORS);
		this.doRenderBlock(buffer, image, address +8, x, y +8, SHADOW_COLORS);
	}
}
