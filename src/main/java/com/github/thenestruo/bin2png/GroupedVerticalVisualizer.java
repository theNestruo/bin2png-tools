package com.github.thenestruo.bin2png;

import java.awt.image.BufferedImage;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;

public class GroupedVerticalVisualizer extends VerticalVisualizer {

	protected final int targetWidth;
	protected final int groupSize;
	protected final int repeat;

	public GroupedVerticalVisualizer(int width, int height, int repeat, int spacing) {
		super(height, spacing);

		Validate.isTrue((width % 8) == 0, "Width %d is not a mutiple of 8", width);

		this.targetWidth = width;
		this.groupSize = width * height / 8;
		this.repeat = repeat;
	}

	@Override
	protected int computeImageWidth(int size) {

		return this.targetWidth;
	}

	@Override
	protected int computeImageHeight(int size) {

		return (this.targetHeight * repeat) + (this.hSpacing * repeat);
	}

	@Override
	protected void renderBlock(byte[] buffer, BufferedImage image, int address) {

		if (address >= this.groupSize * repeat) {
			return;
		}

		super.renderBlock(buffer, image, address);
	}

	@Override
	protected Pair<Integer, Integer> locationFor(final int address) {

		final int group = address / this.groupSize;
		final int column = (address % this.groupSize) / this.targetHeight;
		final int row = (address % this.groupSize) % this.targetHeight;

		final int x = column * 8;
		final int y = group * (this.targetHeight + this.hSpacing) + row;

		return Pair.of(x, y);
	}
}
