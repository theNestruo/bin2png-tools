package com.github.thenestruo.bin2png;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

public class ZxColorVisualizer extends ZxMonochromeVisualizer {

	// Yazioh palette
	protected static final List<Integer> TMS_COLORS = Arrays.asList(
		0x404040, //  0 transparent
		0x000000, //  1 black
		0x3EB849, //  2 medium green
		0x74D07D, //  3 light green
		0x5955E0, //  4 dark blue
		0x8076F1, //  5 light blue
		0xB95E51, //  6 dark red
		0x65DBEF, //  7 cyan
		0xDB6559, //  8 medium red
		0xFF897D, //  9 light red
		0xCCC35E, // 10 dark yellow
		0xDED087, // 11 light yellow
		0x3AA241, // 12 dark green
		0xB766B5, // 13 magenta
		0xCCCCCC, // 14 gray
		0xFFFFFF  // 15 white
	);

	public ZxColorVisualizer(final int width, final int height, final int imageCount, final int spacing) {
		super(width, height, imageCount, spacing);
	}

	@Override
	protected Pair<Integer, Integer> colorsFor(byte[] buffer, int address) {

		int colorValue = this.valueAt(buffer, address + this.totalImageSize);
		return Pair.of(
			TMS_COLORS.get(colorValue >> 4),
			TMS_COLORS.get(colorValue & 0x0F));
	}
}
