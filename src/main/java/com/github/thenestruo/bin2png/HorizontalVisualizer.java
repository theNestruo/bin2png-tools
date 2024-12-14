package com.github.thenestruo.bin2png;

import org.apache.commons.lang3.tuple.Pair;

public class HorizontalVisualizer extends AbstractBlockSupportVisualizer {

	protected final int targetWidth;
	protected final int vSpacing;

	public HorizontalVisualizer(final int targetWidth, final int vSpacing) {
		super();

		this.targetWidth = targetWidth;
		this.vSpacing = vSpacing;
	}

	@Override
	protected int computeImageWidth(final int size) {

		return this.targetWidth;
	}

	@Override
	protected int computeImageHeight(final int size) {

		final int rows = ((size - 1) / this.targetWidth) + 1;
		return (rows * 8) + ((rows - 1) * this.vSpacing);
	}

	@Override
	protected Pair<Integer, Integer> locationFor(final int address) {

		final int row = address / this.targetWidth;
		final int x = (address & 0xfff8) % this.targetWidth;
		final int y = row * (8 + this.vSpacing);
		return Pair.of(x, y);
	}
}
