package com.github.thenestruo.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
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
	public FileSystemResource(String path) {
		this(new File(Validate.notBlank(path, "The path must not be null nor blank")));
	}

	/**
	 * Constructor
	 * @param file the file
	 */
	public FileSystemResource(File file) {
		super();

		this.file = Validate.notNull(file, "The file must not be null");
	}

	@Override
	public InputStream getInputStream() {

		try {
			return file.exists() && file.canRead()
					? new FileInputStream(file)
					: null;

		} catch (FileNotFoundException e) {
			return null;
		}
	}

	@Override
	public long sizeOf() {

		if (file == null) {
			return -1;
		}

		try {
			return FileUtils.sizeOf(file);

		} catch (IllegalArgumentException e) {
			return -1;
		}
	}
}
