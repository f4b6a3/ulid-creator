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

public final class UlidValidator {

	protected static final int STRING_LENGTH = 26;

	protected static final long[] BASE32_VALUES = new long[128];
	static {
		for (int i = 0; i < BASE32_VALUES.length; i++) {
			BASE32_VALUES[i] = -1;
		}
		// Numbers
		BASE32_VALUES['0'] = 0x00;
		BASE32_VALUES['1'] = 0x01;
		BASE32_VALUES['2'] = 0x02;
		BASE32_VALUES['3'] = 0x03;
		BASE32_VALUES['4'] = 0x04;
		BASE32_VALUES['5'] = 0x05;
		BASE32_VALUES['6'] = 0x06;
		BASE32_VALUES['7'] = 0x07;
		BASE32_VALUES['8'] = 0x08;
		BASE32_VALUES['9'] = 0x09;
		// Lower case
		BASE32_VALUES['a'] = 0x0a;
		BASE32_VALUES['b'] = 0x0b;
		BASE32_VALUES['c'] = 0x0c;
		BASE32_VALUES['d'] = 0x0d;
		BASE32_VALUES['e'] = 0x0e;
		BASE32_VALUES['f'] = 0x0f;
		BASE32_VALUES['g'] = 0x10;
		BASE32_VALUES['h'] = 0x11;
		BASE32_VALUES['j'] = 0x12;
		BASE32_VALUES['k'] = 0x13;
		BASE32_VALUES['m'] = 0x14;
		BASE32_VALUES['n'] = 0x15;
		BASE32_VALUES['p'] = 0x16;
		BASE32_VALUES['q'] = 0x17;
		BASE32_VALUES['r'] = 0x18;
		BASE32_VALUES['s'] = 0x19;
		BASE32_VALUES['t'] = 0x1a;
		BASE32_VALUES['v'] = 0x1b;
		BASE32_VALUES['w'] = 0x1c;
		BASE32_VALUES['x'] = 0x1d;
		BASE32_VALUES['y'] = 0x1e;
		BASE32_VALUES['z'] = 0x1f;
		// Lower case OIL
		BASE32_VALUES['o'] = 0x00;
		BASE32_VALUES['i'] = 0x01;
		BASE32_VALUES['l'] = 0x01;
		// Upper case
		BASE32_VALUES['A'] = 0x0a;
		BASE32_VALUES['B'] = 0x0b;
		BASE32_VALUES['C'] = 0x0c;
		BASE32_VALUES['D'] = 0x0d;
		BASE32_VALUES['E'] = 0x0e;
		BASE32_VALUES['F'] = 0x0f;
		BASE32_VALUES['G'] = 0x10;
		BASE32_VALUES['H'] = 0x11;
		BASE32_VALUES['J'] = 0x12;
		BASE32_VALUES['K'] = 0x13;
		BASE32_VALUES['M'] = 0x14;
		BASE32_VALUES['N'] = 0x15;
		BASE32_VALUES['P'] = 0x16;
		BASE32_VALUES['Q'] = 0x17;
		BASE32_VALUES['R'] = 0x18;
		BASE32_VALUES['S'] = 0x19;
		BASE32_VALUES['T'] = 0x1a;
		BASE32_VALUES['V'] = 0x1b;
		BASE32_VALUES['W'] = 0x1c;
		BASE32_VALUES['X'] = 0x1d;
		BASE32_VALUES['Y'] = 0x1e;
		BASE32_VALUES['Z'] = 0x1f;
		// Upper case OIL
		BASE32_VALUES['O'] = 0x00;
		BASE32_VALUES['I'] = 0x01;
		BASE32_VALUES['L'] = 0x01;

	}

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
		return (c.length - hyphen) == STRING_LENGTH;
	}
}
