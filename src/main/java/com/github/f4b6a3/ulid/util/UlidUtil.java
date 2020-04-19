/*
 * MIT License
 * 
 * Copyright (c) 2020 Fabio Lima
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
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

package com.github.f4b6a3.ulid.util;

import java.time.Instant;

import com.github.f4b6a3.util.Base32Util;

public class UlidUtil {

	private UlidUtil() {
	}

	public static long extractTimestamp(String ulid) {
		UlidValidator.validate(ulid);
		return extractUnixMilliseconds(ulid);
	}

	public static Instant extractInstant(String ulid) {
		long milliseconds = extractTimestamp(ulid);
		return Instant.ofEpochMilli(milliseconds);
	}

	public static String extractTimestampComponent(String ulid) {
		UlidValidator.validate(ulid);
		return ulid.substring(0, 10);
	}

	public static String extractRandomnessComponent(String ulid) {
		UlidValidator.validate(ulid);
		return ulid.substring(10, 26);
	}

	protected static long extractUnixMilliseconds(String ulid) {
		String milliseconds = ulid.substring(0, 10);
		return Base32Util.fromBase32CrockfordAsLong(milliseconds);
	}
}
