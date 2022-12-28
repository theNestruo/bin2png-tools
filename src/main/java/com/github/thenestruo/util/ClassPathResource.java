package com.github.thenestruo.util;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * A readable classpath resource
 */
public class ClassPathResource implements ReadableResource {

	private final String path;

	/**
	 * Constructor
	 * @param path the path of the classpath resource
	 * @throws IOException if the classpath resource does not exists
	 */
	public ClassPathResource(String path) {
		super();

		this.path = Validate.notBlank(path, "The path must not be null nor blank");

		// Checks existence
		try (InputStream is = this.getInputStream()) {
			// (no-op)
		} catch (IOException e) {
			ExceptionUtils.rethrow(e);
		}
	}

	@Override
	public InputStream getInputStream() {

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		return classLoader.getResourceAsStream(path);
	}

	@Override
	public long sizeOf() {

		try (InputStream is = this.getInputStream()) {
			return IOUtils.consume(is);

		} catch (IOException e) {
			return -1;
		}
	}
}
