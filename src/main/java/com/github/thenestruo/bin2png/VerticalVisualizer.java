package com.github.thenestruo.bin2png;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;

public class VerticalVisualizer extends AbstractBlockSupportVisualizer {

	protected static final int DEFAULT_HEIGHT = 256;

	protected final int targetHeight;
	protected final int hSpacing;

	public VerticalVisualizer(
			final int targetHeight,
			final int hSpacing) {
		super();

		this.targetHeight = ObjectUtils.defaultIfNull(targetHeight, DEFAULT_HEIGHT);
		this.hSpacing = hSpacing;

		Validate.isTrue((this.targetHeight % 8) == 0, "Height %d is not a mutiple of 8", this.targetHeight);
	}

	@Override
	protected int computeImageWidth(final int size) {

		final int columns = ((size - 1) / this.targetHeight) + 1;
		return (columns * 8) + ((columns - 1) * this.hSpacing);
	}

	@Override
	protected int computeImageHeight(final int size) {

		return this.targetHeight;
	}

	@Override
	protected Pair<Integer, Integer> locationFor(final int address) {

		final int column = address / this.targetHeight;
		final int x = column * (8 + this.hSpacing);
		final int y = address % this.targetHeight;
		return Pair.of(x, y);
	}
}
