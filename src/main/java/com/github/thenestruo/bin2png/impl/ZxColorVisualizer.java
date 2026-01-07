package com.github.thenestruo.bin2png.impl;

import com.github.thenestruo.bin2png.TmsColors;
import com.github.thenestruo.commons.maps.Pair;

public class ZxColorVisualizer extends ZxMonochromeVisualizer {

	public ZxColorVisualizer(final Integer width, final Integer height, final int imageCount, final int spacing) {
		super(width, height, imageCount, spacing);
	}

	@Override
	protected Pair<Integer, Integer> colorsFor(final byte[] buffer, final int address) {

		final int colorValue = this.valueAt(buffer, address + this.totalImageSize);
		return Pair.of(
				TmsColors.TMS_COLORS.get(colorValue >> 4),
				TmsColors.TMS_COLORS.get(colorValue & 0x0F));
	}
}
