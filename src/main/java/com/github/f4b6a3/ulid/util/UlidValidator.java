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
		return (ulid != null && ulid.length() != 0 && isValidString(ulid.toCharArray()));
	}

	/**
	 * Checks if the char array is a valid ULID.
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
	 * @param ulid a ULID char array
	 * @return boolean true if valid
	 */
	public static boolean isValid(char[] ulid) {
		return (ulid != null && ulid.length != 0 && isValidString(ulid));
	}

	/**
	 * Checks if the ULID string is valid.
	 * 
	 * See {@link UlidValidator#isValid(String)}.
	 * 
	 * @param ulid a ULID string
	 * @throws InvalidUlidException if invalid
	 */
	public static void validate(String ulid) {
		if (ulid == null || ulid.length() == 0 || !isValidString(ulid.toCharArray())) {
			throw new InvalidUlidException("Invalid ULID: \"" + ulid + "\"");
		}
	}

	/**
	 * Checks if the ULID char array is valid.
	 * 
	 * See {@link UlidValidator#isValid(String)}.
	 * 
	 * @param ulid a ULID char array
	 * @throws InvalidUlidException if invalid
	 */
	public static void validate(char[] ulid) {
		if (ulid == null || ulid.length == 0 || !isValidString(ulid)) {
			throw new InvalidUlidException("Invalid ULID: \"" + (ulid == null ? null : new String(ulid)) + "\"");
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

		// the two extra bits added by base-32 encoding must be zero
		if ((BASE32_VALUES[c[0]] & 0b11000) != 0) {
			return false; // overflow
		}

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
}
