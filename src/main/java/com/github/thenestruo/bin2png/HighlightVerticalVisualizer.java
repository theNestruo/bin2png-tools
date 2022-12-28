package com.github.thenestruo.bin2png;

import java.io.IOException;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.tuple.Pair;

public class HighlightVerticalVisualizer extends VerticalVisualizer {

	protected static final Pair<Integer, Integer> PADDING_COLORS = Pair.of(0x838482, 0x0f0f0f);
	protected static final Pair<Integer, Integer> CALL_COLORS = Pair.of(0x569CD6, 0x264f78);
	protected static final Pair<Integer, Integer> JUMP_COLORS = Pair.of(0x569CD6, 0x252526);
	protected static final Pair<Integer, Integer> ASCII_COLORS = Pair.of(0xB5CEA8, 0x252526);
	protected static final Pair<Integer, Integer> MUTED_COLORS = Pair.of(0x434442, 0x1e1e1e);

	public HighlightVerticalVisualizer(
			final int targetHeight,
			final int hSpacing) throws IOException {
		super(targetHeight, hSpacing);
	}

	@Override
	protected Pair<Integer, Integer> colorsFor(byte[] buffer, int address) {

		return isPadding(buffer, address) ? PADDING_COLORS
				: isAscii(buffer, address) ? ASCII_COLORS
				: (isZ80Call(buffer, address)
						|| isZ80Call(buffer, address + 1)
						|| isZ80Call(buffer, address + 2)) ? CALL_COLORS
				: (isZ80Jump(buffer, address)
						|| isZ80Jump(buffer, address + 1)
						|| isZ80Jump(buffer, address + 2)) ? JUMP_COLORS
				: MUTED_COLORS;
	}

	private boolean isPadding(byte[] buffer, int address) {

		final int value = valueAt(buffer, address);
		return isPadding(value)
				&& valueAt(buffer, address - 1) == value
				&& valueAt(buffer, address + 1) == value;
	}

	private boolean isAscii(byte[] buffer, int address) {

		return isAscii(valueAt(buffer, address - 1))
				&& isAscii(valueAt(buffer, address))
				&& isAscii(valueAt(buffer, address + 1));
	}

	private boolean isZ80Call(byte[] buffer, int address) {

		int value = valueAt(buffer, address);
		return (value >= 0x40)
				&& (value <= 0xbf)
				&& ArrayUtils.contains(
					new int[]{
						0xcd, 0xdc, 0xfc, 0xd4, 0xc4, 0xf4, 0xec, 0xe4, 0xcc
					},
					valueAt(buffer, address - 2));
	}

	private boolean isZ80Jump(byte[] buffer, int address) {

		int value = valueAt(buffer, address);
		return (value >= 0x40)
				&& (value <= 0xbf)
				&& ArrayUtils.contains(
					new int[]{
						0xc3, 0xda, 0xfa, 0xd2, 0xc2, 0xf2, 0xea, 0xe2, 0xca
					},
					valueAt(buffer, address - 2));
	}

	private boolean isPadding(int value) {

		return (value == 0x00) || (value == 0xff);
	}

	private boolean isAscii(int value) {

		return CharUtils.isAsciiPrintable((char) value);
	}
}
