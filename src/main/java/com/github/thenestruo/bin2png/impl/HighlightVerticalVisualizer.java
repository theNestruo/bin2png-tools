package com.github.thenestruo.bin2png.impl;

import com.github.thenestruo.commons.Chars;
import com.github.thenestruo.commons.IntArrays;
import com.github.thenestruo.commons.maps.Pair;

public class HighlightVerticalVisualizer extends VerticalVisualizer {

	protected static final Pair<Integer, Integer> PADDING_COLORS = Pair.of(0x838482, 0x0f0f0f);
	protected static final Pair<Integer, Integer> CALL_COLORS = Pair.of(0x569CD6, 0x264f78);
	protected static final Pair<Integer, Integer> JUMP_COLORS = Pair.of(0x569CD6, 0x252526);
	protected static final Pair<Integer, Integer> ASCII_COLORS = Pair.of(0xB5CEA8, 0x252526);
	protected static final Pair<Integer, Integer> MUTED_COLORS = Pair.of(0x434442, 0x1e1e1e);

	public HighlightVerticalVisualizer(final Integer targetHeight, final int hSpacing) {
		super(targetHeight, hSpacing);
	}

	@Override
	protected Pair<Integer, Integer> colorsFor(final byte[] buffer, final int address) {

		return this.isPadding(buffer, address) ? PADDING_COLORS
				: this.isAscii(buffer, address) ? ASCII_COLORS
						: (this.isZ80Call(buffer, address)
								|| this.isZ80Call(buffer, address + 1)
								|| this.isZ80Call(buffer, address + 2)) ? CALL_COLORS
										: (this.isZ80Jump(buffer, address)
												|| this.isZ80Jump(buffer, address + 1)
												|| this.isZ80Jump(buffer, address + 2))
														? JUMP_COLORS
														: MUTED_COLORS;
	}

	private boolean isPadding(final byte[] buffer, final int address) {

		for (int i = -2; i <= 0; i++) {
			boolean allPadding0 = true;
			boolean allPadding1 = true;
			for (int j = i; (j <= (i + 2)) && (allPadding0 || allPadding1); j++) {
				final int value = this.valueAt(buffer, address + j);
				allPadding0 &= this.isPadding0(value);
				allPadding1 &= this.isPadding1(value);
			}
			if (allPadding0 || allPadding1) {
				return true;
			}
		}
		return false;
	}

	private boolean isAscii(final byte[] buffer, final int address) {

		for (int i = -2; i <= 0; i++) {
			boolean allAscii = true;
			for (int j = i; (j <= (i + 2)) && allAscii; j++) {
				allAscii &= this.isAscii(this.valueAt(buffer, address + j));
			}
			if (allAscii) {
				return true;
			}
		}
		return false;
	}

	private boolean isZ80Call(final byte[] buffer, final int address) {

		final int value = this.valueAt(buffer, address);
		return (value >= 0x40)
				&& (value <= 0xbf)
				&& IntArrays.contains(
						new int[] { 0xcd, 0xdc, 0xfc, 0xd4, 0xc4, 0xf4, 0xec, 0xe4, 0xcc },
						this.valueAt(buffer, address - 2));
	}

	private boolean isZ80Jump(final byte[] buffer, final int address) {

		final int value = this.valueAt(buffer, address);
		return (value >= 0x40)
				&& (value <= 0xbf)
				&& IntArrays.contains(
						new int[] { 0xc3, 0xda, 0xfa, 0xd2, 0xc2, 0xf2, 0xea, 0xe2, 0xca },
						this.valueAt(buffer, address - 2));
	}

	private boolean isPadding0(final int value) {

		return (value == 0x00);
	}

	private boolean isPadding1(final int value) {

		return (value == 0xff);
	}

	private boolean isAscii(final int value) {

		return Chars.isAsciiPrintable((char) value);
	}
}
