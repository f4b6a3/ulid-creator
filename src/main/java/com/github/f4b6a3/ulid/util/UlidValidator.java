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

import static com.github.f4b6a3.ulid.util.internal.UlidStruct.BASE32_VALUES;

public final class UlidValidator {

	// Date: 10889-08-02T05:31:50.655Z (epoch time: 281474976710655)
	protected static final long TIMESTAMP_MAX = (long) Math.pow(2, 48) - 1;

	protected static final int ULID_LENGTH = 26;

	private UlidValidator() {
	}

	/**
	 * Checks if the string is a valid ULID.
	 * 
	 * A valid ULID string is a sequence of 26 characters from Crockford's base 32
	 * alphabet.
	 * 
	 * It also checks if the timestamp is between 0 and 2^48-1.
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
		char[] chars = ulid.toCharArray();
		return isValidString(chars) && isValidTimestamp(chars);
	}

	/**
	 * Checks if the ULID string is a valid.
	 * 
	 * See {@link UlidValidator#isValid(String)}.
	 * 
	 * @param ulid a ULID string
	 * @throws InvalidUlidException if invalid
	 */
	public static void validate(String ulid) {
		if (!isValid(ulid)) {
			throw new InvalidUlidException(String.format("Invalid ULID: %s.", ulid));
		}
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
	 * @param c a char array
	 * @return boolean true if valid
	 */
	protected static boolean isValidString(final char[] c) {
		int hyphen = 0;
		for (int i = 0; i < c.length; i++) {
			if (c[i] == '-') {
				hyphen++;
				continue;
			}
			if (c[i] == 'U' || c[i] == 'u') {
				return false;
			}
			// ASCII codes: A-Z, 0-9, a-z
			if (!((c[i] >= 0x41 && c[i] <= 0x5a) || (c[i] >= 0x30 && c[i] <= 0x39) || (c[i] >= 0x61 && c[i] <= 0x7a))) {
				return false;
			}
		}
		return (c.length - hyphen) == ULID_LENGTH;
	}

	/**
	 * Checks if the timestamp is between 0 and 2^48-1
	 * 
	 * @param chars a char array
	 * @return false if invalid.
	 */
	protected static boolean isValidTimestamp(char[] chars) {

		long time = 0;

		time |= BASE32_VALUES[chars[0x00]] << 45;
		time |= BASE32_VALUES[chars[0x01]] << 40;
		time |= BASE32_VALUES[chars[0x02]] << 35;
		time |= BASE32_VALUES[chars[0x03]] << 30;
		time |= BASE32_VALUES[chars[0x04]] << 25;
		time |= BASE32_VALUES[chars[0x05]] << 20;
		time |= BASE32_VALUES[chars[0x06]] << 15;
		time |= BASE32_VALUES[chars[0x07]] << 10;
		time |= BASE32_VALUES[chars[0x08]] << 5;
		time |= BASE32_VALUES[chars[0x09]];

		return time >= 0 && time <= TIMESTAMP_MAX;
	}
}
