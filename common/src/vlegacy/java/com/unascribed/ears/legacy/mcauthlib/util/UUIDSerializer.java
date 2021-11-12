/**
 * MCAuthLib
 * 
 * Copyright (C) 2013-2021 Steveice10
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.unascribed.ears.legacy.mcauthlib.util;

import java.util.UUID;

/**
 * Utility class for serializing and deserializing UUIDs.
 */
public class UUIDSerializer {

	/**
	 * Converts a UUID to a String.
	 *
	 * @param value UUID to convert.
	 * @return The resulting String.
	 */
	public static String fromUUID(UUID value) {
		if(value == null) {
			return "";
		}

		return value.toString().replace("-", "");
	}

	/**
	 * Converts a String to a UUID.
	 *
	 * @param value String to convert.
	 * @return The resulting UUID.
	 */
	public static UUID fromString(String value) {
		if(value == null || value.equals("")) {
			return null;
		}

		return UUID.fromString(value.replaceFirst("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
	}
}
