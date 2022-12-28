package com.github.thenestruo.bin2png;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.apache.commons.lang3.tuple.Pair;

public class SpritesVerticalVisualizer extends VerticalVisualizer {

	protected static final Pair<Integer, Integer> SHADOW_COLORS = Pair.of(0x838482, 0x1e1e1e);

	public SpritesVerticalVisualizer(
			final int targetHeight,
			final int hSpacing) throws IOException {
		super(targetHeight, hSpacing);
	}

	protected int computeImageWidth(int size) {

		final int columns = ((size - 1) / this.targetHeight) + 1;
		return (columns * 16) + ((columns - 1) * this.hSpacing);
	}

	protected Pair<Integer, Integer> locationFor(int address) {

		final int column = address / this.targetHeight;
		final int x = column * (16 + hSpacing);
		final int y = address % this.targetHeight;
		return Pair.of(x, y);
	}

	protected void renderBlock(byte[] buffer, BufferedImage image, int address) {

		final Pair<Integer, Integer> location = this.locationFor(address);
		final int x = location.getLeft();
		final int y = location.getRight();
		this.doRenderBlock(buffer, image, address, x, y, DEFAULT_COLORS);
		this.doRenderBlock(buffer, image, address + 16, x + 8, y, SHADOW_COLORS);
	}
}
