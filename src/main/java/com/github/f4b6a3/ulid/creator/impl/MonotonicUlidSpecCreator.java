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

package com.github.f4b6a3.ulid.creator.impl;

import com.github.f4b6a3.ulid.Ulid;
import com.github.f4b6a3.ulid.creator.UlidSpecCreator;

public final class MonotonicUlidSpecCreator extends UlidSpecCreator {

	private long msb = 0;
	private long lsb = 0;

	private long lastTime;

	// 0xffffffffffffffffL + 1 = 0x0000000000000000L
	private static final long UNSIGNED_OVERFLOW = 0x0000000000000000L;

	@Override
	public synchronized Ulid create(final long time) {

		// TODO: test
		if (time == this.lastTime) {
			if (++this.lsb == UNSIGNED_OVERFLOW) {
				// Increment the random bits of the MSB
				this.msb = (this.msb & 0xffffffffffff0000L) | ((this.msb + 1) & 0x000000000000ffffL);
			}
		} else {

			this.msb = 0;
			this.lsb = 0;

			final byte[] bytes = new byte[10];
			this.randomStrategy.nextBytes(bytes);

			this.msb |= time << 16;
			this.msb |= (long) (bytes[0x0] & 0xff) << 8;
			this.msb |= (long) (bytes[0x1] & 0xff);

			this.lsb |= (long) (bytes[0x2] & 0xff) << 56;
			this.lsb |= (long) (bytes[0x3] & 0xff) << 48;
			this.lsb |= (long) (bytes[0x4] & 0xff) << 40;
			this.lsb |= (long) (bytes[0x5] & 0xff) << 32;
			this.lsb |= (long) (bytes[0x6] & 0xff) << 24;
			this.lsb |= (long) (bytes[0x7] & 0xff) << 16;
			this.lsb |= (long) (bytes[0x8] & 0xff) << 8;
			this.lsb |= (long) (bytes[0x9] & 0xff);
		}

		this.lastTime = time;
		return new Ulid(msb, lsb);
	}
}
