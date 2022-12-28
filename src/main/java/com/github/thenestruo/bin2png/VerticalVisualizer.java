package com.github.thenestruo.bin2png;

import java.io.IOException;

import org.apache.commons.lang3.tuple.Pair;

public class VerticalVisualizer extends AbstractVisualizer {

	protected final int targetHeight;
	protected final int hSpacing;

	public VerticalVisualizer(
			final int targetHeight,
			final int hSpacing) throws IOException {
		super();

		this.targetHeight = targetHeight;
		this.hSpacing = hSpacing;
	}

	protected int computeImageWidth(int size) {

		final int columns = ((size - 1) / this.targetHeight) + 1;
		return (columns * 8) + ((columns - 1) * this.hSpacing);
	}

	protected int computeImageHeight(int size) {

		return this.targetHeight;
	}

	protected Pair<Integer, Integer> locationFor(int address) {

		final int column = address / this.targetHeight;
		final int x = column * (8 + hSpacing);
		final int y = address % this.targetHeight;
		return Pair.of(x, y);
	}
}
