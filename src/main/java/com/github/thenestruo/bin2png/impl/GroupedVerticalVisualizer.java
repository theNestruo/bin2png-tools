package com.github.thenestruo.bin2png.impl;

import java.awt.image.BufferedImage;
import java.util.Objects;

import com.github.thenestruo.commons.Bools;
import com.github.thenestruo.commons.maps.Pair;

public class GroupedVerticalVisualizer extends VerticalVisualizer {

	protected final int targetWidth;
	protected final int imageCount;

	protected final int imageSize;
	protected final int totalImageSize;

	public GroupedVerticalVisualizer(final Integer width, final Integer height, final int imageCount,
			final int spacing) {
		super(height, spacing);

		Objects.requireNonNull(width);
		Bools.requireTrue((width % 8) == 0, "Width %d is not a mutiple of 8", width);

		this.targetWidth = width;
		this.imageCount = imageCount;

		this.imageSize = (width * height) / 8;
		this.totalImageSize = this.imageSize * imageCount;
	}

	@Override
	protected int computeImageWidth(final int size) {

		return this.targetWidth;
	}

	@Override
	protected int computeImageHeight(final int size) {

		return (this.targetHeight * this.imageCount) + (this.hSpacing * (this.imageCount - 1));
	}

	@Override
	protected void renderBlock(final byte[] buffer, final BufferedImage image, final int address) {

		if (address >= this.totalImageSize) {
			// (ignored)
			return;
		}

		super.renderBlock(buffer, image, address);
	}

	@Override
	protected Pair<Integer, Integer> locationFor(final int address) {

		final int image = address / this.imageSize;
		final int column = (address % this.imageSize) / this.targetHeight;
		final int row = (address % this.imageSize) % this.targetHeight;

		final int x = column * 8;
		final int y = (image * (this.targetHeight + this.hSpacing)) + row;

		return Pair.of(x, y);
	}
}
