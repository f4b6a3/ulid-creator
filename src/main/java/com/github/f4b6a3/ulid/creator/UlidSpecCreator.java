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

package com.github.f4b6a3.ulid.creator;

import java.util.Random;

import com.github.f4b6a3.ulid.Ulid;
import com.github.f4b6a3.ulid.strategy.RandomStrategy;
import com.github.f4b6a3.ulid.strategy.random.DefaultRandomStrategy;
import com.github.f4b6a3.ulid.strategy.random.OtherRandomStrategy;
import com.github.f4b6a3.ulid.strategy.TimestampStrategy;
import com.github.f4b6a3.ulid.strategy.timestamp.DefaultTimestampStrategy;

public abstract class UlidSpecCreator {

	protected TimestampStrategy timestampStrategy;
	protected RandomStrategy randomStrategy;

	public UlidSpecCreator() {
		this.timestampStrategy = new DefaultTimestampStrategy();
		this.randomStrategy = new DefaultRandomStrategy();
	}

	public synchronized Ulid create() {
		return create(null);
	}

	public abstract Ulid create(Long timestamp);

	/**
	 * Used for changing the timestamp strategy.
	 * 
	 * @param timestampStrategy a timestamp strategy
	 * @return {@link UlidSpecCreator}
	 */
	@SuppressWarnings("unchecked")
	public synchronized <T extends UlidSpecCreator> T withTimestampStrategy(TimestampStrategy timestampStrategy) {
		this.timestampStrategy = timestampStrategy;
		return (T) this;
	}

	/**
	 * Replaces the default random strategy with another.
	 * 
	 * The default random strategy uses {@link java.security.SecureRandom}.
	 * 
	 * See {@link Random}.
	 * 
	 * @param random a random generator
	 * @param <T>    the type parameter
	 * @return {@link AbstractRandomBasedUuidCreator}
	 */
	@SuppressWarnings("unchecked")
	public synchronized <T extends UlidSpecCreator> T withRandomStrategy(RandomStrategy randomStrategy) {
		this.randomStrategy = randomStrategy;
		return (T) this;
	}

	/**
	 * Replaces the default random strategy with another that uses the input
	 * {@link Random} instance.
	 * 
	 * It replaces the internal {@link DefaultRandomStrategy} with
	 * {@link OtherRandomStrategy}.
	 * 
	 * @param random a random generator
	 * @return {@link UlidSpecCreator}
	 */
	@SuppressWarnings("unchecked")
	public synchronized <T extends UlidSpecCreator> T withRandomGenerator(Random random) {
		this.randomStrategy = new OtherRandomStrategy(random);
		return (T) this;
	}
}
