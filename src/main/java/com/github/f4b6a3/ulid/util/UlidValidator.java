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

import com.github.f4b6a3.ulid.exception.InvalidUlidException;

import static com.github.f4b6a3.ulid.util.UlidUtil.*;

public final class UlidValidator {

	// Date: 10889-08-02T05:31:50.655Z (epoch time: 281474976710655)
	protected static final long TIMESTAMP_MAX = (long) Math.pow(2, 48) - 1;

	private UlidValidator() {
	}

	/**
	 * Checks if the string is a valid ULID.
	 * 
	 * A valid ULID string is a sequence of 26 characters from Crockford's base 32
	 * alphabet.
	 * 
	 * <pre>
	 * Examples of valid ULID strings:
	 * - 0123456789ABCDEFGHJKMNPKRS (26 alphanumeric, case insensitive, except U)
	 * - 0123456789ABCDEFGHIJKLMNOP (26 alphanumeric, case insensitive, including OIL, except U)
	 * - 0123456789-ABCDEFGHJK-MNPKRS (26 alphanumeric, case insensitive, except U, with hyphens)
	 * - 0123456789-ABCDEFGHIJ-KLMNOP (26 alphanumeric, case insensitive, including OIL, except U, with hyphens)
	 * </pre>
	 * 
	 * @param ulid a ULID
	 * @return boolean true if valid
	 */
	public static boolean isValid(String ulid) {

		if (ulid == null) {
			return false;
		}

		char[] chars = removeHyphens(ulid.toCharArray());
		if (chars.length != ULID_CHAR_LENGTH || !isCrockfordBase32(chars)) {
			return false;
		}

		// Extract time component
		final char[] timestampComponent = new char[10];
		System.arraycopy(chars, 0, timestampComponent, 0, 10);
		final long timestamp = fromBase32Crockford(timestampComponent);

		return timestamp >= 0 && timestamp <= TIMESTAMP_MAX;

	}

	/**
	 * Checks if the ULID string is a valid.
	 * 
	 * See {@link TsidValidator#isValid(String)}.
	 * 
	 * @param ulid a ULID string
	 * @throws InvalidUlidException if invalid
	 */
	protected static void validate(String ulid) {
		if (!isValid(ulid)) {
			throw new InvalidUlidException(String.format("Invalid ULID: %s.", ulid));
		}
	}
}
