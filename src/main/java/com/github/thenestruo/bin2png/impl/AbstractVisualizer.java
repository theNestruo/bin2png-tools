package com.github.thenestruo.bin2png.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

import com.github.thenestruo.commons.Bools;
import com.github.thenestruo.commons.io.ReadableResource;

public abstract class AbstractVisualizer {

	public final BufferedImage renderImage(final ReadableResource resource) throws IOException {

		Objects.requireNonNull(resource);

		// Reads the data buffer
		final byte[] buffer = resource.readAllBytes();
		Bools.requireTrue(buffer.length == (int) resource.sizeOf());

		return this.renderImage(buffer);
	}

	public abstract BufferedImage renderImage(final byte[] buffer) throws IOException;

	protected int valueAt(final byte[] buffer, final int address) {

		return (address >= 0) && (address < buffer.length)
				? Byte.toUnsignedInt(buffer[address])
				: 0x00;
	}
}
