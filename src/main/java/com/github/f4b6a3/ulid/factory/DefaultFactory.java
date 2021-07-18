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

import java.util.Random;

import com.github.f4b6a3.ulid.Ulid;
import com.github.f4b6a3.ulid.random.RandomGenerator;

/**
 * Factory that generates ULIDs.
 * 
 * The random component is always reset to a new random value.
 * 
 * The maximum ULIDs that can be generated per millisecond is 2^80.
 */
public class DefaultFactory extends UlidFactory {

	/**
	 * Use the default {@link java.security.SecureRandom}.
	 */
	public DefaultFactory() {
		super();
	}

	/**
	 * Use a random generator that inherits from {@link Random}.
	 * 
	 * @param random a {@link Random} instance
	 */
	public DefaultFactory(Random random) {
		this(random::nextBytes);
	}

	/**
	 * Use a random generator that inherits from {@link RandomGenerator}.
	 * 
	 * @param randomGenerator a {@link RandomGenerator} instance
	 */
	public DefaultFactory(RandomGenerator randomGenerator) {
		super(randomGenerator);
	}

	/**
	 * Returns a ULID.
	 * 
	 * @param time a specific time
	 * @return a ULID
	 */
	@Override
	public Ulid create(final long time) {
		final byte[] random = new byte[Ulid.RANDOM_BYTES_LENGTH];
		this.randomGenerator.nextBytes(random);
		return new Ulid(time, random);
	}
}
