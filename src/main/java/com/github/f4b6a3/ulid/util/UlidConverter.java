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

import com.github.f4b6a3.util.Base32Util;
import com.github.f4b6a3.util.ByteUtil;

public class UlidConverter {

	private UlidConverter() {
	}

	/**
	 * Convert a UUID to ULID string
	 * 
	 * The returning string is encoded to Crockford's base32.
	 * 
	 * The timestamp and random components are encoded separated.
	 * 
	 * @param uuid a UUID
	 * @return a ULID
	 */
	public static String toString(UUID uuid) {

		final long msb = uuid.getMostSignificantBits();
		final long lsb = uuid.getLeastSignificantBits();

		// Extract timestamp component
		final long timeNumber = (msb >>> 16);
		String timestampComponent = leftPad(Base32Util.toBase32Crockford(timeNumber));

		// Extract randomness component
		byte[] randBytes = new byte[10];
		randBytes[0] = (byte) (msb >>> 8);
		randBytes[1] = (byte) (msb);
		byte[] lsbBytes = ByteUtil.toBytes(lsb);
		System.arraycopy(lsbBytes, 0, randBytes, 2, 8);
		String randomnessComponent = Base32Util.toBase32Crockford(randBytes);

		return timestampComponent + randomnessComponent;
	}

	/**
	 * Converts a ULID string to a UUID.
	 * 
	 * The input string must be encoded to Crockford's base32, following the ULID
	 * specification.
	 * 
	 * The timestamp and random components are decoded separated.
	 * 
	 * An exception is thrown if the ULID string is invalid.
	 * 
	 * @param ulid a ULID
	 * @return a UUID if valid
	 */
	public static UUID fromString(final String ulid) {

		UlidValidator.validate(ulid);

		// Extract timestamp component
		final String timestampComponent = ulid.substring(0, 10);
		final long timeNumber = Base32Util.fromBase32CrockfordAsLong(timestampComponent);

		// Extract randomness component
		final String randomnessComponent = ulid.substring(10, 26);
		byte[] randBytes = Base32Util.fromBase32Crockford(randomnessComponent);
		byte[] lsbBytes = new byte[8];
		System.arraycopy(randBytes, 2, lsbBytes, 0, 8);

		final long msb = (timeNumber << 16) | ((randBytes[0] << 8) & 0x0000ff00L) | ((randBytes[1]) & 0x000000ffL);
		final long lsb = ByteUtil.toNumber(lsbBytes);

		return new UUID(msb, lsb);
	}

	private static String leftPad(String unpadded) {
		return "0000000000".substring(unpadded.length()) + unpadded;
	}
}
