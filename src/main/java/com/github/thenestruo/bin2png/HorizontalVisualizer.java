package com.github.thenestruo.bin2png;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;

public class HorizontalVisualizer extends AbstractBlockSupportVisualizer {

	protected static final int DEFAULT_WIDTH = 256;

	protected final int targetWidth;
	protected final int vSpacing;

	public HorizontalVisualizer(final int vSpacing) {
		this(DEFAULT_WIDTH, vSpacing);
	}

	public HorizontalVisualizer(final Integer targetWidth, final int vSpacing) {
		super();

		this.targetWidth = ObjectUtils.getIfNull(targetWidth, DEFAULT_WIDTH);
		this.vSpacing = vSpacing;

		Validate.isTrue((this.targetWidth % 8) == 0, "Width %d is not a mutiple of 8", this.targetWidth);
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
