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

import com.github.f4b6a3.ulid.creator.MonotonicUlidFactory;
import com.github.f4b6a3.ulid.creator.UlidFactory;
import com.github.f4b6a3.ulid.creator.DefaultUlidFactory;

/**
 * Facade to the ULID factories.
 * 
 * The ULID has two components:
 * 
 * - Time component: a part of 48 bits that represent the amount of milliseconds
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
		return DefaultFactoryHolder.INSTANCE.create();
	}

	/**
	 * Returns a ULID with a specific time.
	 * 
	 * @param time a specific time
	 * @return a ULID
	 */
	public static Ulid getUlid(final long time) {
		return DefaultFactoryHolder.INSTANCE.create(time);
	}

	/**
	 * Returns a Monotonic ULID.
	 * 
	 * The random component is reset to a new value every time the millisecond
	 * changes.
	 * 
	 * If more than one ULID is generated within the same millisecond, the random
	 * component is incremented by one.
	 * 
	 * @return a ULID
	 */
	public static Ulid getMonotonicUlid() {
		return MonotonicFactoryHolder.INSTANCE.create();
	}

	/**
	 * Returns a Monotonic ULID with a specific time.
	 * 
	 * @param time a specific time
	 * @return a ULID
	 */
	public static Ulid getMonotonicUlid(final long time) {
		return MonotonicFactoryHolder.INSTANCE.create(time);
	}

	/**
	 * Returns an instance of the Default ULID factory.
	 * 
	 * @return a ULID factory
	 */
	public static UlidFactory getDefaultFactory() {
		return new DefaultUlidFactory();
	}

	/**
	 * Returns an instance of the Monotonic ULID factory.
	 * 
	 * @return a ULID factory
	 */
	public static UlidFactory getMonotonicFactory() {
		return new MonotonicUlidFactory();
	}

	private static class DefaultFactoryHolder {
		static final UlidFactory INSTANCE = getDefaultFactory();
	}

	private static class MonotonicFactoryHolder {
		static final UlidFactory INSTANCE = getMonotonicFactory();
	}
}
