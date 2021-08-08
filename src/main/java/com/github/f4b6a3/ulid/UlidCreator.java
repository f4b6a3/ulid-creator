/*
 * MIT License
 * 
 * Copyright (c) 2020-2021 Fabio Lima
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

/**
 * A class for generating ULIDs.
 * 
 * The ULID has two components:
 * 
 * - Time component: a part of 48 bits that represent the number of milliseconds
 * since Unix Epoch, 1970-01-01.
 * 
 * - Random component: a byte array of 80 bits that has a random value generated
 * a secure random generator.
 * 
 * The maximum ULIDs that can be generated per millisecond is 2^80.
 */
public final class UlidCreator {

	private UlidCreator() {
	}

	/**
	 * Returns a ULID.
	 * 
	 * The random component is always reset to a new random value.
	 * 
	 * @return a ULID
	 */
	public static Ulid getUlid() {
		return UlidFactoryHolder.INSTANCE.create();
	}

	/**
	 * Returns a ULID with a given time.
	 * 
	 * The time must be the number of milliseconds since 1970-01-01 (Unix epoch).
	 * 
	 * @param time a given time
	 * @return a ULID
	 */
	public static Ulid getUlid(final long time) {
		return UlidFactoryHolder.INSTANCE.create(time);
	}

	/**
	 * Returns a Monotonic ULID.
	 * 
	 * The random component is reset to a new value whenever the time changes.
	 * 
	 * If more than one ULID is generated within the same time, the random component
	 * is incremented by one.
	 * 
	 * @return a ULID
	 */
	public static Ulid getMonotonicUlid() {
		return MonotonicFactoryHolder.INSTANCE.create();
	}

	/**
	 * Returns a Monotonic ULID with a given time.
	 * 
	 * The time must be the number of milliseconds since 1970-01-01 (Unix epoch).
	 * 
	 * The random component is reset to a new value whenever the time changes.
	 * 
	 * If more than one ULID is generated within the same time, the random component
	 * is incremented by one.
	 * 
	 * @param time a given time
	 * @return a ULID
	 */
	public static Ulid getMonotonicUlid(final long time) {
		return MonotonicFactoryHolder.INSTANCE.create(time);
	}

	private static class UlidFactoryHolder {
		static final UlidFactory INSTANCE = UlidFactory.newInstance();
	}

	private static class MonotonicFactoryHolder {
		static final UlidFactory INSTANCE = UlidFactory.newMonotonicInstance();
	}
}
