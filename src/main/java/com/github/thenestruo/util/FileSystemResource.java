package com.github.thenestruo.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.Validate;

/**
 * A readable file system resource
 */
public class FileSystemResource implements ReadableResource {

	private final File file;

	/**
	 * Constructor
	 * @param path the path of the file system resource
	 */
	public FileSystemResource(final String path) {
		this(new File(Validate.notBlank(path, "The path must not be null nor blank")));
	}

	/**
	 * Constructor
	 * @param file the file
	 */
	public FileSystemResource(final File file) {
		super();

		this.file = Validate.notNull(file, "The file must not be null");
	}

	@Override
	public InputStream getInputStream() {

		try {
			return this.file.exists() && this.file.canRead()
					? new FileInputStream(this.file)
					: null;

		} catch (final FileNotFoundException e) {
			return null;
		}
	}

	@Override
	public long sizeOf() {

		if (this.file == null) {
			return -1;
		}

		try {
			return FileUtils.sizeOf(this.file);

		} catch (final IllegalArgumentException e) {
			return -1;
		}
	}
}
