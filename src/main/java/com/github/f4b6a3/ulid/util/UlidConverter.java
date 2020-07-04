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

import static com.github.f4b6a3.ulid.util.UlidUtil.*;

public final class UlidConverter {

	private UlidConverter() {
	}

	/**
	 * Convert a UUID to ULID string
	 * 
	 * The returning string is encoded to Crockford's base32.
	 * 
	 * @param uuid a UUID
	 * @return a ULID
	 */
	public static String toString(UUID uuid) {

		final long msb = uuid.getMostSignificantBits();
		final long lsb = uuid.getLeastSignificantBits();

		final long time = ((msb & 0xffffffffffff0000L) >>> 16);
		final long random1 = ((msb & 0x000000000000ffffL) << 24) | ((lsb & 0xffffff0000000000L) >>> 40);
		final long random2 = (lsb & 0x000000ffffffffffL);

		final char[] timeComponent = zerofill(toBase32Crockford(time), 10);
		final char[] randomComponent1 = zerofill(toBase32Crockford(random1), 8);
		final char[] randomComponent2 = zerofill(toBase32Crockford(random2), 8);

		char[] output = new char[ULID_CHAR_LENGTH];
		System.arraycopy(timeComponent, 0, output, 0, 10);
		System.arraycopy(randomComponent1, 0, output, 10, 8);
		System.arraycopy(randomComponent2, 0, output, 18, 8);

		return new String(output);
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

		UlidValidator.validate(ulid);
		
		final char[] input = ulid.toCharArray(); 
		final char[] timeComponent = new char[10];
		final char[] randomComponent1 = new char[8];
		final char[] randomComponent2 = new char[8];
		
		System.arraycopy(input, 0, timeComponent, 0, 10);
		System.arraycopy(input, 10, randomComponent1, 0, 8);
		System.arraycopy(input, 18, randomComponent2, 0, 8);

		final long time = fromBase32Crockford(timeComponent);
		final long random1 = fromBase32Crockford(randomComponent1);
		final long random2 = fromBase32Crockford(randomComponent2);

		final long msb = ((time & 0x0000ffffffffffffL) << 16) | ((random1 & 0x000000ffff000000L) >>> 24);
		final long lsb = ((random1 & 0x0000000000ffffffL) << 40) | (random2 & 0x000000ffffffffffL);

		return new UUID(msb, lsb);
	}

}
