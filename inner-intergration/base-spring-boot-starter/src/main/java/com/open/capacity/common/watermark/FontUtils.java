package com.open.capacity.common.watermark;

import java.util.stream.Stream;

public class FontUtils {

	/**
	 * 将指定的字符串重复repeats次.
	 */
	public  static String repeatString(String pattern, int repeats) {
		StringBuilder buffer = new StringBuilder(pattern.length() * repeats);
		Stream.generate(() -> pattern).limit(repeats).forEach(buffer::append);
		return new String(buffer);
	}
	
}
