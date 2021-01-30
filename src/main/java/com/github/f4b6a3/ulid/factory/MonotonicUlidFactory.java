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

package com.github.f4b6a3.ulid.factory;

import com.github.f4b6a3.ulid.Ulid;

/**
 * Factory that generates Monotonic ULIDs.
 * 
 * The random component is reset to a new value every time the millisecond changes.
 * 
 * If more than one ULID is generated within the same millisecond, the random
 * component is incremented by one.
 * 
 * The maximum ULIDs that can be generated per millisecond is 2^80.
 */
public final class MonotonicUlidFactory extends UlidFactory {

	private long lastTime = -1;
	private Ulid lastUlid = null;

	/**
	 * Returns a ULID.
	 * 
	 * @param time a specific time
	 * @return a ULID
	 */
	@Override
	public synchronized Ulid create(final long time) {

		if (time == this.lastTime) {
			this.lastUlid = lastUlid.increment();
		} else {
			final byte[] random = new byte[Ulid.RANDOM_BYTES_LENGTH];
			this.randomGenerator.nextBytes(random);
			this.lastUlid = new Ulid(time, random);
		}

		this.lastTime = time;
		return new Ulid(this.lastUlid);
	}
}
