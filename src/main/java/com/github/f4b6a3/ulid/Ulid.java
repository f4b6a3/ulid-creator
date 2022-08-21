/*
 * MIT License
 * 
 * Copyright (c) 2020-2022 Fabio Lima
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

package com.github.f4b6a3.ulid;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * This class represents a ULID.
 * 
 * The ULID has two components:
 * 
 * - Time component: a part of 48 bits that represent the amount of milliseconds
 * since Unix Epoch, 1970-01-01.
 * 
 * - Random component: a byte array of 80 bits that has a random value generated
 * a secure random generator.
 * 
 * Instances of this class are immutable.
 */
public final class Ulid implements Serializable, Comparable<Ulid> {

	private static final long serialVersionUID = 2625269413446854731L;

	private final long msb; // most significant bits
	private final long lsb; // least significant bits

	public static final int ULID_CHARS = 26;
	public static final int TIME_CHARS = 10;
	public static final int RANDOM_CHARS = 16;

	public static final int ULID_BYTES = 16;
	public static final int TIME_BYTES = 6;
	public static final int RANDOM_BYTES = 10;

	private static final char[] ALPHABET_UPPERCASE = //
			{ '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', //
					'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', //
					'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'V', 'W', 'X', 'Y', 'Z' };

	private static final char[] ALPHABET_LOWERCASE = //
			{ '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', //
					'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', //
					'm', 'n', 'p', 'q', 'r', 's', 't', 'v', 'w', 'x', 'y', 'z' };

	private static final long[] ALPHABET_VALUES = new long[128];
	static {
		for (int i = 0; i < ALPHABET_VALUES.length; i++) {
			ALPHABET_VALUES[i] = -1;
		}
		// Numbers
		ALPHABET_VALUES['0'] = 0x00;
		ALPHABET_VALUES['1'] = 0x01;
		ALPHABET_VALUES['2'] = 0x02;
		ALPHABET_VALUES['3'] = 0x03;
		ALPHABET_VALUES['4'] = 0x04;
		ALPHABET_VALUES['5'] = 0x05;
		ALPHABET_VALUES['6'] = 0x06;
		ALPHABET_VALUES['7'] = 0x07;
		ALPHABET_VALUES['8'] = 0x08;
		ALPHABET_VALUES['9'] = 0x09;
		// Lower case
		ALPHABET_VALUES['a'] = 0x0a;
		ALPHABET_VALUES['b'] = 0x0b;
		ALPHABET_VALUES['c'] = 0x0c;
		ALPHABET_VALUES['d'] = 0x0d;
		ALPHABET_VALUES['e'] = 0x0e;
		ALPHABET_VALUES['f'] = 0x0f;
		ALPHABET_VALUES['g'] = 0x10;
		ALPHABET_VALUES['h'] = 0x11;
		ALPHABET_VALUES['j'] = 0x12;
		ALPHABET_VALUES['k'] = 0x13;
		ALPHABET_VALUES['m'] = 0x14;
		ALPHABET_VALUES['n'] = 0x15;
		ALPHABET_VALUES['p'] = 0x16;
		ALPHABET_VALUES['q'] = 0x17;
		ALPHABET_VALUES['r'] = 0x18;
		ALPHABET_VALUES['s'] = 0x19;
		ALPHABET_VALUES['t'] = 0x1a;
		ALPHABET_VALUES['v'] = 0x1b;
		ALPHABET_VALUES['w'] = 0x1c;
		ALPHABET_VALUES['x'] = 0x1d;
		ALPHABET_VALUES['y'] = 0x1e;
		ALPHABET_VALUES['z'] = 0x1f;
		// Lower case OIL
		ALPHABET_VALUES['o'] = 0x00;
		ALPHABET_VALUES['i'] = 0x01;
		ALPHABET_VALUES['l'] = 0x01;
		// Upper case
		ALPHABET_VALUES['A'] = 0x0a;
		ALPHABET_VALUES['B'] = 0x0b;
		ALPHABET_VALUES['C'] = 0x0c;
		ALPHABET_VALUES['D'] = 0x0d;
		ALPHABET_VALUES['E'] = 0x0e;
		ALPHABET_VALUES['F'] = 0x0f;
		ALPHABET_VALUES['G'] = 0x10;
		ALPHABET_VALUES['H'] = 0x11;
		ALPHABET_VALUES['J'] = 0x12;
		ALPHABET_VALUES['K'] = 0x13;
		ALPHABET_VALUES['M'] = 0x14;
		ALPHABET_VALUES['N'] = 0x15;
		ALPHABET_VALUES['P'] = 0x16;
		ALPHABET_VALUES['Q'] = 0x17;
		ALPHABET_VALUES['R'] = 0x18;
		ALPHABET_VALUES['S'] = 0x19;
		ALPHABET_VALUES['T'] = 0x1a;
		ALPHABET_VALUES['V'] = 0x1b;
		ALPHABET_VALUES['W'] = 0x1c;
		ALPHABET_VALUES['X'] = 0x1d;
		ALPHABET_VALUES['Y'] = 0x1e;
		ALPHABET_VALUES['Z'] = 0x1f;
		// Upper case OIL
		ALPHABET_VALUES['O'] = 0x00;
		ALPHABET_VALUES['I'] = 0x01;
		ALPHABET_VALUES['L'] = 0x01;
	}

	// 0xffffffffffffffffL + 1 = 0x0000000000000000L
	private static final long INCREMENT_OVERFLOW = 0x0000000000000000L;

	/**
	 * Create a new ULID.
	 * 
	 * Useful to make copies of ULIDs.
	 * 
	 * @param ulid a ULID
	 */
	public Ulid(Ulid ulid) {
		this.msb = ulid.msb;
		this.lsb = ulid.lsb;
	}

	/**
	 * Create a new ULID.
	 * 
	 * @param mostSignificantBits  the first 8 bytes as a long value
	 * @param leastSignificantBits the last 8 bytes as a long value
	 */
	public Ulid(long mostSignificantBits, long leastSignificantBits) {
		this.msb = mostSignificantBits;
		this.lsb = leastSignificantBits;
	}

	/**
	 * Create a new ULID.
	 * 
	 * @param time   the time component in milliseconds since 1970-01-01
	 * @param random the random component in byte array
	 */
	public Ulid(long time, byte[] random) {

		// The time component has 48 bits.
		if ((time & 0xffff000000000000L) != 0) {
			// ULID specification:
			// "Any attempt to decode or encode a ULID larger than this (time > 2^48-1)
			// should be rejected by all implementations, to prevent overflow bugs."
			throw new IllegalArgumentException("Invalid time value"); // time overflow!
		}
		// The random component has 80 bits (10 bytes).
		if (random == null || random.length != RANDOM_BYTES) {
			throw new IllegalArgumentException("Invalid random bytes"); // null or wrong length!
		}

		long long0 = 0;
		long long1 = 0;

		long0 |= time << 16;
		long0 |= (long) (random[0x0] & 0xff) << 8;
		long0 |= (long) (random[0x1] & 0xff);

		long1 |= (long) (random[0x2] & 0xff) << 56;
		long1 |= (long) (random[0x3] & 0xff) << 48;
		long1 |= (long) (random[0x4] & 0xff) << 40;
		long1 |= (long) (random[0x5] & 0xff) << 32;
		long1 |= (long) (random[0x6] & 0xff) << 24;
		long1 |= (long) (random[0x7] & 0xff) << 16;
		long1 |= (long) (random[0x8] & 0xff) << 8;
		long1 |= (long) (random[0x9] & 0xff);

		this.msb = long0;
		this.lsb = long1;
	}

	/**
	 * Converts a UUID into a ULID.
	 * 
	 * @param uuid a UUID
	 * @return a ULID
	 */
	public static Ulid from(UUID uuid) {
		return new Ulid(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits());
	}

	/**
	 * Converts a byte array into a ULID.
	 * 
	 * @param bytes a byte array
	 * @return a ULID
	 */
	public static Ulid from(byte[] bytes) {

		if (bytes == null || bytes.length != ULID_BYTES) {
			throw new IllegalArgumentException("Invalid ULID bytes"); // null or wrong length!
		}

		long msb = 0;
		long lsb = 0;

		msb |= (bytes[0x0] & 0xffL) << 56;
		msb |= (bytes[0x1] & 0xffL) << 48;
		msb |= (bytes[0x2] & 0xffL) << 40;
		msb |= (bytes[0x3] & 0xffL) << 32;
		msb |= (bytes[0x4] & 0xffL) << 24;
		msb |= (bytes[0x5] & 0xffL) << 16;
		msb |= (bytes[0x6] & 0xffL) << 8;
		msb |= (bytes[0x7] & 0xffL);

		lsb |= (bytes[0x8] & 0xffL) << 56;
		lsb |= (bytes[0x9] & 0xffL) << 48;
		lsb |= (bytes[0xa] & 0xffL) << 40;
		lsb |= (bytes[0xb] & 0xffL) << 32;
		lsb |= (bytes[0xc] & 0xffL) << 24;
		lsb |= (bytes[0xd] & 0xffL) << 16;
		lsb |= (bytes[0xe] & 0xffL) << 8;
		lsb |= (bytes[0xf] & 0xffL);

		return new Ulid(msb, lsb);
	}

	/**
	 * Converts a canonical string into a ULID.
	 * 
	 * The input string must be 26 characters long and must contain only characters
	 * from Crockford's base 32 alphabet.
	 * 
	 * The first character of the input string must be between 0 and 7.
	 * 
	 * @param string a canonical string
	 * @return a ULID
	 */
	public static Ulid from(String string) {

		final char[] chars = toCharArray(string);

		long time = 0;
		long random0 = 0;
		long random1 = 0;

		time |= ALPHABET_VALUES[chars[0x00]] << 45;
		time |= ALPHABET_VALUES[chars[0x01]] << 40;
		time |= ALPHABET_VALUES[chars[0x02]] << 35;
		time |= ALPHABET_VALUES[chars[0x03]] << 30;
		time |= ALPHABET_VALUES[chars[0x04]] << 25;
		time |= ALPHABET_VALUES[chars[0x05]] << 20;
		time |= ALPHABET_VALUES[chars[0x06]] << 15;
		time |= ALPHABET_VALUES[chars[0x07]] << 10;
		time |= ALPHABET_VALUES[chars[0x08]] << 5;
		time |= ALPHABET_VALUES[chars[0x09]];

		random0 |= ALPHABET_VALUES[chars[0x0a]] << 35;
		random0 |= ALPHABET_VALUES[chars[0x0b]] << 30;
		random0 |= ALPHABET_VALUES[chars[0x0c]] << 25;
		random0 |= ALPHABET_VALUES[chars[0x0d]] << 20;
		random0 |= ALPHABET_VALUES[chars[0x0e]] << 15;
		random0 |= ALPHABET_VALUES[chars[0x0f]] << 10;
		random0 |= ALPHABET_VALUES[chars[0x10]] << 5;
		random0 |= ALPHABET_VALUES[chars[0x11]];

		random1 |= ALPHABET_VALUES[chars[0x12]] << 35;
		random1 |= ALPHABET_VALUES[chars[0x13]] << 30;
		random1 |= ALPHABET_VALUES[chars[0x14]] << 25;
		random1 |= ALPHABET_VALUES[chars[0x15]] << 20;
		random1 |= ALPHABET_VALUES[chars[0x16]] << 15;
		random1 |= ALPHABET_VALUES[chars[0x17]] << 10;
		random1 |= ALPHABET_VALUES[chars[0x18]] << 5;
		random1 |= ALPHABET_VALUES[chars[0x19]];

		final long msb = (time << 16) | (random0 >>> 24);
		final long lsb = (random0 << 40) | (random1 & 0xffffffffffL);

		return new Ulid(msb, lsb);
	}

	/**
	 * Convert the ULID into a UUID.
	 * 
	 * If you need a RFC-4122 UUID v4 do this: {@code Ulid.toRfc4122().toUuid()}.
	 * 
	 * @return a UUID.
	 */
	public UUID toUuid() {
		return new UUID(this.msb, this.lsb);
	}

	/**
	 * Convert the ULID into a byte array.
	 * 
	 * @return an byte array.
	 */
	public byte[] toBytes() {

		final byte[] bytes = new byte[ULID_BYTES];

		bytes[0x0] = (byte) (msb >>> 56);
		bytes[0x1] = (byte) (msb >>> 48);
		bytes[0x2] = (byte) (msb >>> 40);
		bytes[0x3] = (byte) (msb >>> 32);
		bytes[0x4] = (byte) (msb >>> 24);
		bytes[0x5] = (byte) (msb >>> 16);
		bytes[0x6] = (byte) (msb >>> 8);
		bytes[0x7] = (byte) (msb);

		bytes[0x8] = (byte) (lsb >>> 56);
		bytes[0x9] = (byte) (lsb >>> 48);
		bytes[0xa] = (byte) (lsb >>> 40);
		bytes[0xb] = (byte) (lsb >>> 32);
		bytes[0xc] = (byte) (lsb >>> 24);
		bytes[0xd] = (byte) (lsb >>> 16);
		bytes[0xe] = (byte) (lsb >>> 8);
		bytes[0xf] = (byte) (lsb);

		return bytes;
	}

	/**
	 * Converts the ULID into a canonical string in upper case.
	 * 
	 * The output string is 26 characters long and contains only characters from
	 * Crockford's base 32 alphabet.
	 * 
	 * For lower case string, use the shorthand {@code Ulid#toLowerCase()}, instead
	 * of {@code Ulid#toString()#toLowerCase()}.
	 * 
	 * See: https://www.crockford.com/base32.html
	 * 
	 * @return a ULID string
	 */
	@Override
	public String toString() {
		return toString(ALPHABET_UPPERCASE);
	}

	/**
	 * Converts the ULID into a canonical string in lower case.
	 * 
	 * The output string is 26 characters long and contains only characters from
	 * Crockford's base 32 alphabet.
	 * 
	 * It is a shorthand at least twice as fast as
	 * {@code Ulid.toString().toLowerCase()}.
	 * 
	 * See: https://www.crockford.com/base32.html
	 * 
	 * @return a string
	 */
	public String toLowerCase() {
		return toString(ALPHABET_LOWERCASE);
	}

	/**
	 * Converts the ULID into into another ULID that is compatible with UUID v4.
	 * 
	 * The bytes of the returned ULID are compliant with the RFC-4122 version 4.
	 * 
	 * If you need a RFC-4122 UUID v4 do this: {@code Ulid.toRfc4122().toUuid()}.
	 * 
	 * Read: https://tools.ietf.org/html/rfc4122
	 * 
	 * ### RFC-4122 - 4.4. Algorithms for Creating a UUID from Truly Random or
	 * Pseudo-Random Numbers
	 * 
	 * The version 4 UUID is meant for generating UUIDs from truly-random or
	 * pseudo-random numbers.
	 * 
	 * The algorithm is as follows:
	 * 
	 * - Set the two most significant bits (bits 6 and 7) of the
	 * clock_seq_hi_and_reserved to zero and one, respectively.
	 * 
	 * - Set the four most significant bits (bits 12 through 15) of the
	 * time_hi_and_version field to the 4-bit version number from Section 4.1.3.
	 * 
	 * - Set all the other bits to randomly (or pseudo-randomly) chosen values.
	 * 
	 * @return a ULID
	 */
	public Ulid toRfc4122() {

		// set the 4 most significant bits of the 7th byte to 0, 1, 0 and 0
		final long msb4 = (this.msb & 0xffffffffffff0fffL) | 0x0000000000004000L; // RFC-4122 version 4
		// set the 2 most significant bits of the 9th byte to 1 and 0
		final long lsb4 = (this.lsb & 0x3fffffffffffffffL) | 0x8000000000000000L; // RFC-4122 variant 2

		return new Ulid(msb4, lsb4);
	}

	/**
	 * Returns the instant of creation.
	 * 
	 * The instant of creation is extracted from the time component.
	 * 
	 * @return {@link Instant}
	 */
	public Instant getInstant() {
		return Instant.ofEpochMilli(this.getTime());
	}

	/**
	 * Returns the instant of creation.
	 * 
	 * The instant of creation is extracted from the time component.
	 * 
	 * @param string a canonical string
	 * @return {@link Instant}
	 */
	public static Instant getInstant(String string) {
		return Instant.ofEpochMilli(getTime(string));
	}

	/**
	 * Returns the time component as a number.
	 * 
	 * The time component is a number between 0 and 2^48-1. It is equivalent to the
	 * count of milliseconds since 1970-01-01 (Unix epoch).
	 * 
	 * @return a number of milliseconds.
	 */
	public long getTime() {
		return this.msb >>> 16;
	}

	/**
	 * Returns the time component as a number.
	 * 
	 * The time component is a number between 0 and 2^48-1. It is equivalent to the
	 * count of milliseconds since 1970-01-01 (Unix epoch).
	 * 
	 * @param string a canonical string
	 * @return a number of milliseconds.
	 */
	public static long getTime(String string) {

		final char[] chars = toCharArray(string);

		long time = 0;

		time |= ALPHABET_VALUES[chars[0x00]] << 45;
		time |= ALPHABET_VALUES[chars[0x01]] << 40;
		time |= ALPHABET_VALUES[chars[0x02]] << 35;
		time |= ALPHABET_VALUES[chars[0x03]] << 30;
		time |= ALPHABET_VALUES[chars[0x04]] << 25;
		time |= ALPHABET_VALUES[chars[0x05]] << 20;
		time |= ALPHABET_VALUES[chars[0x06]] << 15;
		time |= ALPHABET_VALUES[chars[0x07]] << 10;
		time |= ALPHABET_VALUES[chars[0x08]] << 5;
		time |= ALPHABET_VALUES[chars[0x09]];

		return time;
	}

	/**
	 * Returns the random component as a byte array.
	 * 
	 * The random component is an array of 10 bytes (80 bits).
	 * 
	 * @return a byte array
	 */
	public byte[] getRandom() {

		final byte[] bytes = new byte[RANDOM_BYTES];

		bytes[0x0] = (byte) (msb >>> 8);
		bytes[0x1] = (byte) (msb);

		bytes[0x2] = (byte) (lsb >>> 56);
		bytes[0x3] = (byte) (lsb >>> 48);
		bytes[0x4] = (byte) (lsb >>> 40);
		bytes[0x5] = (byte) (lsb >>> 32);
		bytes[0x6] = (byte) (lsb >>> 24);
		bytes[0x7] = (byte) (lsb >>> 16);
		bytes[0x8] = (byte) (lsb >>> 8);
		bytes[0x9] = (byte) (lsb);

		return bytes;
	}

	/**
	 * Returns the random component as a byte array.
	 * 
	 * The random component is an array of 10 bytes (80 bits).
	 * 
	 * @param string a canonical string
	 * @return a byte array
	 */
	public static byte[] getRandom(String string) {

		final char[] chars = toCharArray(string);

		long random0 = 0;
		long random1 = 0;

		random0 |= ALPHABET_VALUES[chars[0x0a]] << 35;
		random0 |= ALPHABET_VALUES[chars[0x0b]] << 30;
		random0 |= ALPHABET_VALUES[chars[0x0c]] << 25;
		random0 |= ALPHABET_VALUES[chars[0x0d]] << 20;
		random0 |= ALPHABET_VALUES[chars[0x0e]] << 15;
		random0 |= ALPHABET_VALUES[chars[0x0f]] << 10;
		random0 |= ALPHABET_VALUES[chars[0x10]] << 5;
		random0 |= ALPHABET_VALUES[chars[0x11]];

		random1 |= ALPHABET_VALUES[chars[0x12]] << 35;
		random1 |= ALPHABET_VALUES[chars[0x13]] << 30;
		random1 |= ALPHABET_VALUES[chars[0x14]] << 25;
		random1 |= ALPHABET_VALUES[chars[0x15]] << 20;
		random1 |= ALPHABET_VALUES[chars[0x16]] << 15;
		random1 |= ALPHABET_VALUES[chars[0x17]] << 10;
		random1 |= ALPHABET_VALUES[chars[0x18]] << 5;
		random1 |= ALPHABET_VALUES[chars[0x19]];

		final byte[] bytes = new byte[RANDOM_BYTES];

		bytes[0x0] = (byte) (random0 >>> 32);
		bytes[0x1] = (byte) (random0 >>> 24);
		bytes[0x2] = (byte) (random0 >>> 16);
		bytes[0x3] = (byte) (random0 >>> 8);
		bytes[0x4] = (byte) (random0);

		bytes[0x5] = (byte) (random1 >>> 32);
		bytes[0x6] = (byte) (random1 >>> 24);
		bytes[0x7] = (byte) (random1 >>> 16);
		bytes[0x8] = (byte) (random1 >>> 8);
		bytes[0x9] = (byte) (random1);

		return bytes;
	}

	/**
	 * Returns the most significant bits as a number.
	 * 
	 * @return a number.
	 */
	public long getMostSignificantBits() {
		return this.msb;
	}

	/**
	 * Returns the least significant bits as a number.
	 * 
	 * @return a number.
	 */
	public long getLeastSignificantBits() {
		return this.lsb;
	}

	/**
	 * Returns a new ULID by incrementing the random component of the current ULID.
	 * 
	 * Since the random component contains 80 bits:
	 * 
	 * (1) This method can generate up to 1208925819614629174706176 (2^80) ULIDs per
	 * millisecond;
	 * 
	 * (2) This method can generate monotonic increasing ULIDs 99.999999999999992%
	 * ((2^80 - 10^9) / (2^80)) of the time within a single millisecond interval,
	 * considering an unrealistic rate of 1,000,000,000 ULIDs per millisecond.
	 * 
	 * Due to (1) and (2), it does not throw the error message recommended by the
	 * specification. When an overflow occurs in the random 80 bits, the time
	 * component is simply incremented.
	 * 
	 * @return a ULID
	 */
	public Ulid increment() {

		long newMsb = this.msb;
		long newLsb = this.lsb + 1; // increment the LEAST significant bits

		if (newLsb == INCREMENT_OVERFLOW) {
			newMsb += 1; // increment the MOST significant bits
		}

		return new Ulid(newMsb, newLsb);
	}

	/**
	 * Checks if the input string is valid.
	 * 
	 * The input string must be 26 characters long and must contain only characters
	 * from Crockford's base 32 alphabet.
	 * 
	 * The first character of the input string must be between 0 and 7.
	 * 
	 * @param string a string
	 * @return true if valid
	 */
	public static boolean isValid(String string) {
		return string != null && isValidCharArray(string.toCharArray());
	}

	@Override
	public int hashCode() {
		final long bits = msb ^ lsb;
		return (int) (bits ^ (bits >>> 32));
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj.getClass() != Ulid.class)
			return false;
		Ulid that = (Ulid) obj;
		if (lsb != that.lsb)
			return false;
		if (msb != that.msb)
			return false;
		return true;
	}

	@Override
	public int compareTo(Ulid that) {

		// used to compare as UNSIGNED longs
		final long min = 0x8000000000000000L;

		final long a = this.msb + min;
		final long b = that.msb + min;

		if (a > b)
			return 1;
		else if (a < b)
			return -1;

		final long c = this.lsb + min;
		final long d = that.lsb + min;

		if (c > d)
			return 1;
		else if (c < d)
			return -1;

		return 0;
	}

	protected String toString(char[] alphabet) {

		final char[] chars = new char[ULID_CHARS];

		long time = this.msb >>> 16;
		long random0 = ((this.msb & 0xffffL) << 24) | (this.lsb >>> 40);
		long random1 = (this.lsb & 0xffffffffffL);

		chars[0x00] = alphabet[(int) (time >>> 45 & 0b11111)];
		chars[0x01] = alphabet[(int) (time >>> 40 & 0b11111)];
		chars[0x02] = alphabet[(int) (time >>> 35 & 0b11111)];
		chars[0x03] = alphabet[(int) (time >>> 30 & 0b11111)];
		chars[0x04] = alphabet[(int) (time >>> 25 & 0b11111)];
		chars[0x05] = alphabet[(int) (time >>> 20 & 0b11111)];
		chars[0x06] = alphabet[(int) (time >>> 15 & 0b11111)];
		chars[0x07] = alphabet[(int) (time >>> 10 & 0b11111)];
		chars[0x08] = alphabet[(int) (time >>> 5 & 0b11111)];
		chars[0x09] = alphabet[(int) (time & 0b11111)];

		chars[0x0a] = alphabet[(int) (random0 >>> 35 & 0b11111)];
		chars[0x0b] = alphabet[(int) (random0 >>> 30 & 0b11111)];
		chars[0x0c] = alphabet[(int) (random0 >>> 25 & 0b11111)];
		chars[0x0d] = alphabet[(int) (random0 >>> 20 & 0b11111)];
		chars[0x0e] = alphabet[(int) (random0 >>> 15 & 0b11111)];
		chars[0x0f] = alphabet[(int) (random0 >>> 10 & 0b11111)];
		chars[0x10] = alphabet[(int) (random0 >>> 5 & 0b11111)];
		chars[0x11] = alphabet[(int) (random0 & 0b11111)];

		chars[0x12] = alphabet[(int) (random1 >>> 35 & 0b11111)];
		chars[0x13] = alphabet[(int) (random1 >>> 30 & 0b11111)];
		chars[0x14] = alphabet[(int) (random1 >>> 25 & 0b11111)];
		chars[0x15] = alphabet[(int) (random1 >>> 20 & 0b11111)];
		chars[0x16] = alphabet[(int) (random1 >>> 15 & 0b11111)];
		chars[0x17] = alphabet[(int) (random1 >>> 10 & 0b11111)];
		chars[0x18] = alphabet[(int) (random1 >>> 5 & 0b11111)];
		chars[0x19] = alphabet[(int) (random1 & 0b11111)];

		return new String(chars);
	}

	protected static char[] toCharArray(String string) {
		char[] chars = string == null ? null : string.toCharArray();
		if (!isValidCharArray(chars)) {
			throw new IllegalArgumentException(String.format("Invalid ULID: \"%s\"", string));
		}
		return chars;
	}

	/**
	 * Checks if the string is a valid ULID.
	 * 
	 * A valid ULID string is a sequence of 26 characters from Crockford's base 32
	 * alphabet.
	 * 
	 * The first character of the input string must be between 0 and 7.
	 * 
	 * @param chars a char array
	 * @return boolean true if valid
	 */
	protected static boolean isValidCharArray(final char[] chars) {

		if (chars == null || chars.length != ULID_CHARS) {
			return false; // null or wrong size!
		}

		// The time component has 48 bits.
		// The base32 encoded time component has 50 bits.
		// The time component cannot be greater than than 2^48-1.
		// So the 2 first bits of the base32 decoded time component must be ZERO.
		// As a consequence, the 1st char of the input string must be between 0 and 7.
		if ((ALPHABET_VALUES[chars[0]] & 0b11000) != 0) {
			// ULID specification:
			// "Any attempt to decode or encode a ULID larger than this (time > 2^48-1)
			// should be rejected by all implementations, to prevent overflow bugs."
			return false; // time overflow!
		}

		for (int i = 0; i < chars.length; i++) {
			if (ALPHABET_VALUES[chars[i]] == -1) {
				return false; // invalid character!
			}
		}

		return true; // It seems to be OK.
	}
}
