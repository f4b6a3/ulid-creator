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

import java.util.UUID;

import com.github.f4b6a3.ulid.exception.InvalidUlidException;
import com.github.f4b6a3.ulid.util.internal.UlidStruct;

public final class UlidConverter {

	private UlidConverter() {
	}

	/**
	 * Convert a UUID to ULID string
	 * 
	 * The returning string is encoded to Crockford's base32.
	 * 
	 * @param ulid a UUID
	 * @return a ULID
	 */
	public static String toString(final UUID ulid) {
		UlidStruct struct = new UlidStruct(ulid);
		return struct.toString();
	}

	/**
	 * Converts a ULID string to a UUID.
	 * 
	 * The input string must be encoded to Crockford's base32, following the ULID
	 * specification.
	 * 
	 * An exception is thrown if the ULID string is invalid.
	 * 
	 * @param ulid a ULID
	 * @return a UUID if valid
	 * @throws InvalidUlidException if invalid
	 */
	public static UUID fromString(final String ulid) {
		UlidStruct struct = new UlidStruct(ulid);
		return struct.toUuid();
	}

}
