package com.github.thenestruo.bin2png;

import java.awt.image.BufferedImage;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;

public class GroupedVerticalVisualizer extends VerticalVisualizer {

	protected final int targetWidth;
	protected final int imageCount;

	protected final int imageSize;
	protected final int totalImageSize;

	public GroupedVerticalVisualizer(int width, int height, int imageCount, int spacing) {
		super(height, spacing);

		Validate.isTrue((width % 8) == 0, "Width %d is not a mutiple of 8", width);

		this.targetWidth = width;
		this.imageCount = imageCount;

		this.imageSize = width * height / 8;
		this.totalImageSize = this.imageSize * imageCount;
	}

	@Override
	protected int computeImageWidth(int size) {

		return this.targetWidth;
	}

	@Override
	protected int computeImageHeight(int size) {

		return (this.targetHeight * imageCount) + (this.hSpacing * (imageCount - 1));
	}

	@Override
	protected void renderBlock(byte[] buffer, BufferedImage image, int address) {

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
		final int y = image * (this.targetHeight + this.hSpacing) + row;

		return Pair.of(x, y);
	}
}
