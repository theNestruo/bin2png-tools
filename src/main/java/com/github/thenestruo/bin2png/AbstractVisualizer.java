package com.github.thenestruo.bin2png;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;

import com.github.thenestruo.util.ReadableResource;

public abstract class AbstractVisualizer {

	public final BufferedImage renderImage(final ReadableResource resource) throws IOException {

		Objects.requireNonNull(resource);

		// Reads the data buffer
		final byte[] buffer;
		try (final InputStream is = IOUtils.buffer(resource.getInputStream())) {
			buffer = IOUtils.toByteArray(is);
		}
		Validate.isTrue(buffer.length == (int) resource.sizeOf());

		return this.renderImage(buffer);
	}

	public abstract BufferedImage renderImage(final byte[] buffer) throws IOException;

	protected int valueAt(final byte[] buffer, final int address) {

		return (address >= 0) && (address < buffer.length)
				? Byte.toUnsignedInt(buffer[address])
				: 0x00;
	}
}
