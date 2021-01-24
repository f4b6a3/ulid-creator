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

public final class DefaultUlidSpecCreator extends UlidSpecCreator {

	@Override
	public synchronized Ulid create(final long time) {

		long msb = 0;
		long lsb = 0;

		final byte[] bytes = new byte[10];
		this.randomStrategy.nextBytes(bytes);

		msb |= time << 16;
		msb |= (long) (bytes[0x0] & 0xff) << 8;
		msb |= (long) (bytes[0x1] & 0xff);

		lsb |= (long) (bytes[0x2] & 0xff) << 56;
		lsb |= (long) (bytes[0x3] & 0xff) << 48;
		lsb |= (long) (bytes[0x4] & 0xff) << 40;
		lsb |= (long) (bytes[0x5] & 0xff) << 32;
		lsb |= (long) (bytes[0x6] & 0xff) << 24;
		lsb |= (long) (bytes[0x7] & 0xff) << 16;
		lsb |= (long) (bytes[0x8] & 0xff) << 8;
		lsb |= (long) (bytes[0x9] & 0xff);

		return new Ulid(msb, lsb);
	}
}
