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

package com.github.f4b6a3.ulid;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * This class represents a ULID.
 */
public final class Ulid implements Serializable, Comparable<Ulid> {

	private final long msb;
	private final long lsb;

	protected static final long TIME_MAX = 281474976710655L; // 2^48 - 1

	public static final int ULID_LENGTH = 26;
	public static final int TIME_LENGTH = 10;
	public static final int RANDOM_LENGTH = 16;

	public static final int ULID_BYTES_LENGTH = 16;
	public static final int TIME_BYTES_LENGTH = 6;
	public static final int RANDOM_BYTES_LENGTH = 10;

	// 0xffffffffffffffffL + 1 = 0x0000000000000000L
	private static final long INCREMENT_OVERFLOW = 0x0000000000000000L;

	protected static final char[] ALPHABET_UPPERCASE = //
			{ '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', //
					'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', //
					'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'V', 'W', 'X', 'Y', 'Z' };

	protected static final char[] ALPHABET_LOWERCASE = //
			{ '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', //
					'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', //
					'm', 'n', 'p', 'q', 'r', 's', 't', 'v', 'w', 'x', 'y', 'z' };

	protected static final long[] ALPHABET_VALUES = new long[128];
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

	private static final long serialVersionUID = 2625269413446854731L;

	public Ulid(long mostSignificantBits, long leastSignificantBits) {
		this.msb = mostSignificantBits;
		this.lsb = leastSignificantBits;
	}

	public static Ulid of(Ulid ulid) {
		return new Ulid(ulid.getMostSignificantBits(), ulid.getLeastSignificantBits());
	}

	public static Ulid of(UUID uuid) {
		return new Ulid(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits());
	}

	// TODO: test
	public static Ulid of(byte[] bytes) {

		if (bytes == null || bytes.length != ULID_BYTES_LENGTH) {
			throw new IllegalArgumentException("Invalid ULID bytes");
		}

		long long0 = 0;
		long long1 = 0;

		long0 |= (bytes[0x0] & 0xffL) << 56;
		long0 |= (bytes[0x1] & 0xffL) << 48;
		long0 |= (bytes[0x2] & 0xffL) << 40;
		long0 |= (bytes[0x3] & 0xffL) << 32;
		long0 |= (bytes[0x4] & 0xffL) << 24;
		long0 |= (bytes[0x5] & 0xffL) << 16;
		long0 |= (bytes[0x6] & 0xffL) << 8;
		long0 |= (bytes[0x7] & 0xffL);

		long1 |= (bytes[0x8] & 0xffL) << 56;
		long1 |= (bytes[0x9] & 0xffL) << 48;
		long1 |= (bytes[0xa] & 0xffL) << 40;
		long1 |= (bytes[0xb] & 0xffL) << 32;
		long1 |= (bytes[0xc] & 0xffL) << 24;
		long1 |= (bytes[0xd] & 0xffL) << 16;
		long1 |= (bytes[0xe] & 0xffL) << 8;
		long1 |= (bytes[0xf] & 0xffL);

		return new Ulid(long0, long1);
	}

	// TODO: optimize
	public static Ulid of(String string) {

		final char[] chars = toCharArray(string);

		long tm = 0;
		long r1 = 0;
		long r2 = 0;

		tm |= ALPHABET_VALUES[chars[0x00]] << 45;
		tm |= ALPHABET_VALUES[chars[0x01]] << 40;
		tm |= ALPHABET_VALUES[chars[0x02]] << 35;
		tm |= ALPHABET_VALUES[chars[0x03]] << 30;
		tm |= ALPHABET_VALUES[chars[0x04]] << 25;
		tm |= ALPHABET_VALUES[chars[0x05]] << 20;
		tm |= ALPHABET_VALUES[chars[0x06]] << 15;
		tm |= ALPHABET_VALUES[chars[0x07]] << 10;
		tm |= ALPHABET_VALUES[chars[0x08]] << 5;
		tm |= ALPHABET_VALUES[chars[0x09]];

		r1 |= ALPHABET_VALUES[chars[0x0a]] << 35;
		r1 |= ALPHABET_VALUES[chars[0x0b]] << 30;
		r1 |= ALPHABET_VALUES[chars[0x0c]] << 25;
		r1 |= ALPHABET_VALUES[chars[0x0d]] << 20;
		r1 |= ALPHABET_VALUES[chars[0x0e]] << 15;
		r1 |= ALPHABET_VALUES[chars[0x0f]] << 10;
		r1 |= ALPHABET_VALUES[chars[0x10]] << 5;
		r1 |= ALPHABET_VALUES[chars[0x11]];

		r2 |= ALPHABET_VALUES[chars[0x12]] << 35;
		r2 |= ALPHABET_VALUES[chars[0x13]] << 30;
		r2 |= ALPHABET_VALUES[chars[0x14]] << 25;
		r2 |= ALPHABET_VALUES[chars[0x15]] << 20;
		r2 |= ALPHABET_VALUES[chars[0x16]] << 15;
		r2 |= ALPHABET_VALUES[chars[0x17]] << 10;
		r2 |= ALPHABET_VALUES[chars[0x18]] << 5;
		r2 |= ALPHABET_VALUES[chars[0x19]];

		final long msb = (tm << 16) | (r1 >>> 24);
		final long lsb = (r1 << 40) | (r2 & 0xffffffffffL);

		return new Ulid(msb, lsb);
	}

	public static Ulid of(long time, byte[] random) {

		if ((time & 0xffff000000000000L) != 0) {
			throw new IllegalArgumentException("Invalid time value");
		}
		if (random == null || random.length != RANDOM_BYTES_LENGTH) {
			throw new IllegalArgumentException("Invalid random bytes");
		}

		long msb = 0;
		long lsb = 0;

		msb |= time << 16;
		msb |= (long) (random[0x0] & 0xff) << 8;
		msb |= (long) (random[0x1] & 0xff);

		lsb |= (long) (random[0x2] & 0xff) << 56;
		lsb |= (long) (random[0x3] & 0xff) << 48;
		lsb |= (long) (random[0x4] & 0xff) << 40;
		lsb |= (long) (random[0x5] & 0xff) << 32;
		lsb |= (long) (random[0x6] & 0xff) << 24;
		lsb |= (long) (random[0x7] & 0xff) << 16;
		lsb |= (long) (random[0x8] & 0xff) << 8;
		lsb |= (long) (random[0x9] & 0xff);

		return new Ulid(msb, lsb);
	}

	public UUID toUuid() {
		return new UUID(this.msb, this.lsb);
	}

	// TODO: test
	public UUID toUuid4() {
		final long msb4 = (this.msb & 0xffffffffffff0fffL) | 0x0000000000004000L; // apply version 4
		final long lsb4 = (this.lsb & 0x3fffffffffffffffL) | 0x8000000000000000L; // apply variant RFC-4122
		return new UUID(msb4, lsb4);
	}

	// TODO: test
	public byte[] toBytes() {

		final byte[] bytes = new byte[ULID_BYTES_LENGTH];

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

	// TODO: test
	public byte[] toBytes4() {
		return Ulid.of(this.toUuid4()).toBytes();
	}

	@Override
	public String toString() {
		return this.toUpperCase();
	}

	// TODO: test
	public String toUpperCase() {
		return toString(ALPHABET_UPPERCASE);
	}

	// TODO: test
	public String toUpperCase4() {
		return Ulid.of(this.toUuid4()).toUpperCase();
	}

	// TODO: test
	public String toLowerCase() {
		return toString(ALPHABET_LOWERCASE);
	}

	// TODO: test
	public String toLowerCase4() {
		return Ulid.of(this.toUuid4()).toLowerCase();
	}

	public long getTime() {
		return this.msb >>> 16;
	}

	public Instant getInstant() {
		return Instant.ofEpochMilli(this.getTime());
	}

	public long getMostSignificantBits() {
		return this.msb;
	}

	public long getLeastSignificantBits() {
		return this.lsb;
	}

	// TODO: test
	public Ulid increment() {

		long msb1 = this.msb;
		long lsb1 = this.lsb + 1; // Increment the LSB

		if (lsb1 == INCREMENT_OVERFLOW) {
			// Increment the random bits of the MSB
			msb1 = (msb1 & 0xffffffffffff0000L) | ((msb1 + 1) & 0x000000000000ffffL);
		}

		return new Ulid(msb1, lsb1);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (lsb ^ (lsb >>> 32));
		result = prime * result + (int) (msb ^ (msb >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Ulid other = (Ulid) obj;
		if (lsb != other.lsb)
			return false;
		if (msb != other.msb)
			return false;
		return true;
	}

	@Override
	public int compareTo(Ulid other) {
		if (this.msb < other.msb)
			return -1;
		if (this.msb > other.msb)
			return 1;
		if (this.lsb < other.lsb)
			return -1;
		if (this.lsb > other.lsb)
			return 1;
		return 0;
	}

	// TODO: optimize
	protected String toString(char[] alphabet) {

		final char[] chars = new char[ULID_LENGTH];

		long time = this.msb >>> 16;
		long random1 = ((this.msb & 0xffffL) << 24) | (this.lsb >>> 40);
		long random2 = (this.lsb & 0xffffffffffL);

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

		chars[0x0a] = alphabet[(int) (random1 >>> 35 & 0b11111)];
		chars[0x0b] = alphabet[(int) (random1 >>> 30 & 0b11111)];
		chars[0x0c] = alphabet[(int) (random1 >>> 25 & 0b11111)];
		chars[0x0d] = alphabet[(int) (random1 >>> 20 & 0b11111)];
		chars[0x0e] = alphabet[(int) (random1 >>> 15 & 0b11111)];
		chars[0x0f] = alphabet[(int) (random1 >>> 10 & 0b11111)];
		chars[0x10] = alphabet[(int) (random1 >>> 5 & 0b11111)];
		chars[0x11] = alphabet[(int) (random1 & 0b11111)];

		chars[0x12] = alphabet[(int) (random2 >>> 35 & 0b11111)];
		chars[0x13] = alphabet[(int) (random2 >>> 30 & 0b11111)];
		chars[0x14] = alphabet[(int) (random2 >>> 25 & 0b11111)];
		chars[0x15] = alphabet[(int) (random2 >>> 20 & 0b11111)];
		chars[0x16] = alphabet[(int) (random2 >>> 15 & 0b11111)];
		chars[0x17] = alphabet[(int) (random2 >>> 10 & 0b11111)];
		chars[0x18] = alphabet[(int) (random2 >>> 5 & 0b11111)];
		chars[0x19] = alphabet[(int) (random2 & 0b11111)];

		return new String(chars);
	}

	public static boolean isValidString(String string) {
		return isValidArray(string == null ? null : string.toCharArray());
	}

	/**
	 * Checks if the string is a valid ULID.
	 * 
	 * A valid ULID string is a sequence of 26 characters from Crockford's base 32
	 * alphabet.
	 * 
	 * @param chars a char array
	 * @return boolean true if valid
	 */
	protected static boolean isValidArray(final char[] chars) {

		if (chars == null || chars.length != ULID_LENGTH) {
			return false; // null or wrong size!
		}

		// the two extra bits added by base-32 encoding must be zero
		if ((ALPHABET_VALUES[chars[0]] & 0b11000) != 0) {
			return false; // overflow!
		}

		for (int i = 0; i < chars.length; i++) {
			if (ALPHABET_VALUES[chars[i]] == -1) {
				return false; // invalid character!
			}
		}

		return true; // It seems to be OK.
	}

	protected static char[] toCharArray(String string) {
		char[] chars = string == null ? new char[0] : string.toCharArray();
		if (!isValidArray(chars)) {
			throw new IllegalArgumentException(String.format("Invalid ULID: \"%s\"", string));
		}
		return chars;
	}
}
