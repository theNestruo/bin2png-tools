package com.github.thenestruo.bin2png;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TmsColors {

	// Yazioh palette
	public static final List<Integer> TMS_COLORS = Collections.unmodifiableList(Arrays.asList(
			0x404040, // 0 transparent
			0x000000, // 1 black
			0x3EB849, // 2 medium green
			0x74D07D, // 3 light green
			0x5955E0, // 4 dark blue
			0x8076F1, // 5 light blue
			0xB95E51, // 6 dark red
			0x65DBEF, // 7 cyan
			0xDB6559, // 8 medium red
			0xFF897D, // 9 light red
			0xCCC35E, // 10 dark yellow
			0xDED087, // 11 light yellow
			0x3AA241, // 12 dark green
			0xB766B5, // 13 magenta
			0xCCCCCC, // 14 gray
			0xFFFFFF // 15 white
	));

	/**
	 * Private default constructor
	 */
	private TmsColors() {
	}
}
