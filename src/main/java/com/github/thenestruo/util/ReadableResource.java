package com.github.thenestruo.util;

import java.io.InputStream;

/**
 * A readable resource
 */
public interface ReadableResource {

	InputStream getInputStream();

	long sizeOf();
}
