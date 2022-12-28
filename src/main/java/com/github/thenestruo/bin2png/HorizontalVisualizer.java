package com.github.thenestruo.bin2png;

import java.io.IOException;

import org.apache.commons.lang3.tuple.Pair;

public class HorizontalVisualizer extends AbstractVisualizer {

	protected final int targetWidth;
	protected final int vSpacing;

	public HorizontalVisualizer(
			final int targetWidth,
			final int vSpacing) throws IOException {
		super();

		this.targetWidth = targetWidth;
		this.vSpacing = vSpacing;
	}

	protected int computeImageWidth(int size) {

		return this.targetWidth;
	}

	protected int computeImageHeight(int size) {

		final int rows = ((size - 1) / this.targetWidth) + 1;
		return (rows * 8) + ((rows - 1) * this.vSpacing);
	}

	protected Pair<Integer, Integer> locationFor(int address) {

		final int row = address / this.targetWidth;
		final int x = (address & 0xfff8) % this.targetWidth;
		final int y = row * (8 + vSpacing);
		return Pair.of(x, y);
	}
}
