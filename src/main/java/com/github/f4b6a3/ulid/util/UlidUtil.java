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
import java.util.UUID;

public final class UlidUtil {

	protected static final int BASE_32 = 32;

	protected static final int ULID_LENGTH = 26;

	// Include 'O'->ZERO, 'I'->ONE and 'L'->ONE
	protected static final char[] ALPHABET_CROCKFORD = "0123456789ABCDEFGHJKMNPQRSTVWXYZOIL".toCharArray();

	protected static final char[] ALPHABET_JAVA = "0123456789abcdefghijklmnopqrstuv011".toCharArray();

	private UlidUtil() {
	}

	public static long extractUnixMilliseconds(UUID ulid) {
		return extractTimestamp(ulid);
	}

	public static long extractUnixMilliseconds(String ulid) {
		UlidValidator.validate(ulid);
		return extractTimestamp(ulid);
	}

	public static Instant extractInstant(UUID ulid) {
		long milliseconds = extractTimestamp(ulid);
		return Instant.ofEpochMilli(milliseconds);
	}

	public static Instant extractInstant(String ulid) {
		UlidValidator.validate(ulid);
		long milliseconds = extractTimestamp(ulid);
		return Instant.ofEpochMilli(milliseconds);
	}
	
	private static long extractTimestamp(UUID ulid) {
		return (ulid.getMostSignificantBits() >>> 16);
	}

	private static long extractTimestamp(String ulid) {
		return fromBase32Crockford(extractTimestampComponent(ulid).toCharArray());
	}

	public static String extractTimestampComponent(String ulid) {
		UlidValidator.validate(ulid);
		return ulid.substring(0, 10);
	}

	public static String extractRandomnessComponent(String ulid) {
		UlidValidator.validate(ulid);
		return ulid.substring(10, ULID_LENGTH);
	}

	/**
	 * Get a number from a given array of bytes.
	 * 
	 * @param bytes a byte array
	 * @return a long
	 */
	public static long toNumber(final byte[] bytes) {
		return toNumber(bytes, 0, bytes.length);
	}

	public static long toNumber(final byte[] bytes, final int start, final int end) {
		long result = 0;
		for (int i = start; i < end; i++) {
			result = (result << 8) | (bytes[i] & 0xff);
		}
		return result;
	}

	/**
	 * Get an array of bytes from a given number.
	 *
	 * @param number a long value
	 * @return a byte array
	 */
	protected static byte[] toBytes(final long number) {
		return new byte[] { (byte) (number >>> 56), (byte) (number >>> 48), (byte) (number >>> 40),
				(byte) (number >>> 32), (byte) (number >>> 24), (byte) (number >>> 16), (byte) (number >>> 8),
				(byte) (number) };
	}

	protected static char[] removeHyphens(final char[] input) {

		int count = 0;
		char[] buffer = new char[input.length];

		for (int i = 0; i < input.length; i++) {
			if ((input[i] != '-')) {
				buffer[count++] = input[i];
			}
		}

		char[] output = new char[count];
		System.arraycopy(buffer, 0, output, 0, count);

		return output;
	}

	public static char[] toBase32Crockford(long number) {
		return encode(number, UlidUtil.ALPHABET_CROCKFORD);
	}

	public static long fromBase32Crockford(char[] chars) {
		return decode(chars, UlidUtil.ALPHABET_CROCKFORD);
	}

	protected static boolean isCrockfordBase32(final char[] chars) {
		char[] input = toUpperCase(chars);
		for (int i = 0; i < input.length; i++) {
			if (!isCrockfordBase32(input[i])) {
				return false;
			}
		}
		return true;
	}

	protected static boolean isCrockfordBase32(char c) {
		for (int j = 0; j < ALPHABET_CROCKFORD.length; j++) {
			if (c == ALPHABET_CROCKFORD[j]) {
				return true;
			}
		}
		return false;
	}

	protected static char[] zerofill(char[] chars, int length) {
		return lpad(chars, length, '0');
	}

	protected static char[] lpad(char[] chars, int length, char fill) {

		int delta = 0;
		int limit = 0;

		if (length > chars.length) {
			delta = length - chars.length;
			limit = length;
		} else {
			delta = 0;
			limit = chars.length;
		}

		char[] output = new char[chars.length + delta];
		for (int i = 0; i < limit; i++) {
			if (i < delta) {
				output[i] = fill;
			} else {
				output[i] = chars[i - delta];
			}
		}
		return output;
	}

	protected static String transliterate(String string, char[] alphabet1, char[] alphabet2) {
		return new String(transliterate(string.toCharArray(), alphabet1, alphabet2));
	}

	protected static char[] transliterate(char[] chars, char[] alphabet1, char[] alphabet2) {
		char[] output = chars.clone();
		for (int i = 0; i < output.length; i++) {
			for (int j = 0; j < alphabet1.length; j++) {
				if (output[i] == alphabet1[j]) {
					output[i] = alphabet2[j];
					break;
				}
			}
		}
		return output;
	}

	protected static char[] toUpperCase(final char[] chars) {
		char[] output = new char[chars.length];
		for (int i = 0; i < output.length; i++) {
			if (chars[i] >= 0x61 && chars[i] <= 0x7a) {
				output[i] = (char) ((int) chars[i] & 0xffffffdf);
			} else {
				output[i] = chars[i];
			}
		}
		return output;
	}

	/**
	 * Encode a long number to base 32 char array.
	 * 
	 * @param number   a long number
	 * @param alphabet an alphabet
	 * @return a base32 encoded char array
	 */
	protected static char[] encode(long number, char[] alphabet) {

		final int CHARS_MAX = 13; // 13 * 5 = 65

		if (number < 0) {
			throw new IllegalArgumentException(String.format("Number '%d' is not a positive integer.", number));
		}

		long n = number;
		char[] buffer = new char[CHARS_MAX];
		char[] output;

		int count = CHARS_MAX;
		while (n > 0) {
			buffer[--count] = alphabet[(int) (n % BASE_32)];
			n = n / BASE_32;
		}

		output = new char[buffer.length - count];
		System.arraycopy(buffer, count, output, 0, output.length);

		return output;
	}

	/**
	 * Decode a base 32 char array to a long number.
	 * 
	 * @param chars    a base 32 encoded char array
	 * @param alphabet an alphabet
	 * @return a long number
	 */
	protected static long decode(char[] chars, char[] alphabet) {

		long n = 0;

		for (int i = 0; i < chars.length; i++) {
			int d = chr(chars[i], alphabet);
			n = BASE_32 * n + d;
		}

		return n;
	}

	private static int chr(char c, char[] alphabet) {
		for (int i = 0; i < alphabet.length; i++) {
			if (alphabet[i] == c) {
				return (byte) i;
			}
		}
		return (byte) '0';
	}
}
