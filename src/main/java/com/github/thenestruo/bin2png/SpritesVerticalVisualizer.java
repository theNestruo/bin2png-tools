package com.github.thenestruo.bin2png;

import java.awt.image.BufferedImage;

import org.apache.commons.lang3.tuple.Pair;

public class SpritesVerticalVisualizer extends VerticalVisualizer {

	protected static final Pair<Integer, Integer> SHADOW_COLORS = Pair.of(0x838482, 0x1e1e1e);

	public SpritesVerticalVisualizer(final Integer targetHeight, final int hSpacing) {
		super(targetHeight, hSpacing);
	}

	@Override
	protected int computeImageWidth(final int size) {

		final int columns = ((size - 1) / this.targetHeight) + 1;
		return (columns * 16) + ((columns - 1) * this.hSpacing);
	}

	@Override
	protected Pair<Integer, Integer> locationFor(final int address) {

		final int column = address / this.targetHeight;
		final int x = column * (16 + this.hSpacing);
		final int y = address % this.targetHeight;
		return Pair.of(x, y);
	}

	@Override
	protected void renderBlock(final byte[] buffer, final BufferedImage image, final int address) {

		final Pair<Integer, Integer> location = this.locationFor(address);
		final int x = location.getLeft();
		final int y = location.getRight();
		this.doRenderBlock(buffer, image, address, x, y, DEFAULT_COLORS);
		this.doRenderBlock(buffer, image, address + 16, x + 8, y, SHADOW_COLORS);
	}
}
