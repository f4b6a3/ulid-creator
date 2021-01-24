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
import java.util.UUID;

import com.github.f4b6a3.ulid.util.UlidValidator;

/**
 * This class represents a ULID.
 */
public final class Ulid implements Serializable, Comparable<Ulid> {

	private final long msb;
	private final long lsb;

	protected static final int STRING_LENGTH = 26;

	protected static final char[] BASE32_CHARS = //
			{ '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', //
					'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', //
					'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'V', 'W', 'X', 'Y', 'Z' };

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

	private static final long serialVersionUID = 2625269413446854731L;

	private Ulid() {
		this.msb = 0;
		this.lsb = 0;
	}

	private Ulid(UUID ulid) {
		this.msb = ulid.getMostSignificantBits();
		this.lsb = ulid.getLeastSignificantBits();
	}

	private Ulid(String ulid) {

		final char[] chars = ulid == null ? new char[0] : ulid.toCharArray();
		UlidValidator.validate(chars);

		long tm = 0;
		long r1 = 0;
		long r2 = 0;

		tm |= BASE32_VALUES[chars[0x00]] << 45;
		tm |= BASE32_VALUES[chars[0x01]] << 40;
		tm |= BASE32_VALUES[chars[0x02]] << 35;
		tm |= BASE32_VALUES[chars[0x03]] << 30;
		tm |= BASE32_VALUES[chars[0x04]] << 25;
		tm |= BASE32_VALUES[chars[0x05]] << 20;
		tm |= BASE32_VALUES[chars[0x06]] << 15;
		tm |= BASE32_VALUES[chars[0x07]] << 10;
		tm |= BASE32_VALUES[chars[0x08]] << 5;
		tm |= BASE32_VALUES[chars[0x09]];

		r1 |= BASE32_VALUES[chars[0x0a]] << 35;
		r1 |= BASE32_VALUES[chars[0x0b]] << 30;
		r1 |= BASE32_VALUES[chars[0x0c]] << 25;
		r1 |= BASE32_VALUES[chars[0x0d]] << 20;
		r1 |= BASE32_VALUES[chars[0x0e]] << 15;
		r1 |= BASE32_VALUES[chars[0x0f]] << 10;
		r1 |= BASE32_VALUES[chars[0x10]] << 5;
		r1 |= BASE32_VALUES[chars[0x11]];

		r2 |= BASE32_VALUES[chars[0x12]] << 35;
		r2 |= BASE32_VALUES[chars[0x13]] << 30;
		r2 |= BASE32_VALUES[chars[0x14]] << 25;
		r2 |= BASE32_VALUES[chars[0x15]] << 20;
		r2 |= BASE32_VALUES[chars[0x16]] << 15;
		r2 |= BASE32_VALUES[chars[0x17]] << 10;
		r2 |= BASE32_VALUES[chars[0x18]] << 5;
		r2 |= BASE32_VALUES[chars[0x19]];

		this.msb = (tm << 16) | (r1 >>> 24);
		this.lsb = (r1 << 40) | (r2 & 0xffffffffffL);
	}

	private Ulid(long msb, long lsb) {
		this.msb = msb;
		this.lsb = lsb;
	}

	public static Ulid of(UUID ulid) {
		return new Ulid(ulid);
	}

	public static Ulid of(String ulid) {
		return new Ulid(ulid);
	}

	public static Ulid of(long msb, long lsb) {
		return new Ulid(msb, lsb);
	}

	public UUID toUuid() {
		return new UUID(this.msb, this.lsb);
	}

	@Override
	public String toString() {

		final char[] chars = new char[STRING_LENGTH];
		long long0 = this.msb;
		long long1 = this.lsb;

		long time = long0 >>> 16;
		long random1 = ((long0 & 0xffffL) << 24) | (long1 >>> 40);
		long random2 = (long1 & 0xffffffffffL);

		chars[0x00] = BASE32_CHARS[(int) (time >>> 45 & 0b11111)];
		chars[0x01] = BASE32_CHARS[(int) (time >>> 40 & 0b11111)];
		chars[0x02] = BASE32_CHARS[(int) (time >>> 35 & 0b11111)];
		chars[0x03] = BASE32_CHARS[(int) (time >>> 30 & 0b11111)];
		chars[0x04] = BASE32_CHARS[(int) (time >>> 25 & 0b11111)];
		chars[0x05] = BASE32_CHARS[(int) (time >>> 20 & 0b11111)];
		chars[0x06] = BASE32_CHARS[(int) (time >>> 15 & 0b11111)];
		chars[0x07] = BASE32_CHARS[(int) (time >>> 10 & 0b11111)];
		chars[0x08] = BASE32_CHARS[(int) (time >>> 5 & 0b11111)];
		chars[0x09] = BASE32_CHARS[(int) (time & 0b11111)];

		chars[0x0a] = BASE32_CHARS[(int) (random1 >>> 35 & 0b11111)];
		chars[0x0b] = BASE32_CHARS[(int) (random1 >>> 30 & 0b11111)];
		chars[0x0c] = BASE32_CHARS[(int) (random1 >>> 25 & 0b11111)];
		chars[0x0d] = BASE32_CHARS[(int) (random1 >>> 20 & 0b11111)];
		chars[0x0e] = BASE32_CHARS[(int) (random1 >>> 15 & 0b11111)];
		chars[0x0f] = BASE32_CHARS[(int) (random1 >>> 10 & 0b11111)];
		chars[0x10] = BASE32_CHARS[(int) (random1 >>> 5 & 0b11111)];
		chars[0x11] = BASE32_CHARS[(int) (random1 & 0b11111)];

		chars[0x12] = BASE32_CHARS[(int) (random2 >>> 35 & 0b11111)];
		chars[0x13] = BASE32_CHARS[(int) (random2 >>> 30 & 0b11111)];
		chars[0x14] = BASE32_CHARS[(int) (random2 >>> 25 & 0b11111)];
		chars[0x15] = BASE32_CHARS[(int) (random2 >>> 20 & 0b11111)];
		chars[0x16] = BASE32_CHARS[(int) (random2 >>> 15 & 0b11111)];
		chars[0x17] = BASE32_CHARS[(int) (random2 >>> 10 & 0b11111)];
		chars[0x18] = BASE32_CHARS[(int) (random2 >>> 5 & 0b11111)];
		chars[0x19] = BASE32_CHARS[(int) (random2 & 0b11111)];

		return new String(chars);
	}

	public long getTimestamp() {
		return this.msb >>> 16;
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
}
