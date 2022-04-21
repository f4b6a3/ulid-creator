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

import java.security.SecureRandom;
import java.time.Clock;
import java.util.Random;
import java.util.function.LongFunction;
import java.util.function.Supplier;

/**
 * Factory that generates ULIDs.
 * 
 * If the factory is not monotonic, the random component always changes.
 * 
 * If the factory is monotonic, the random component changes whenever the
 * millisecond changes. If more than one ULID is generated within the same
 * millisecond, the random component is incremented by one.
 * 
 * The maximum ULIDs that can be generated per millisecond is 2^80.
 */
public final class UlidFactory {

	private final Clock clock; // for tests
	private final LongFunction<Ulid> ulidFunction;

	public UlidFactory() {
		this(new UlidFunction(), null);
	}

	private UlidFactory(LongFunction<Ulid> ulidFunction) {
		this(ulidFunction, null);
	}

	private UlidFactory(LongFunction<Ulid> ulidFunction, Clock clock) {
		this.ulidFunction = ulidFunction;
		this.clock = clock != null ? clock : Clock.systemUTC();
	}

	/**
	 * Returns a new factory.
	 * 
	 * It is equivalent to {@code new UlidFactory()}.
	 * 
	 * @return {@link UlidFactory}
	 */
	public static UlidFactory newInstance() {
		return new UlidFactory(new UlidFunction());
	}

	/**
	 * Returns a new factory.
	 * 
	 * @param random a {@link Random} generator
	 * @return {@link UlidFactory}
	 */
	public static UlidFactory newInstance(Random random) {
		return new UlidFactory(new UlidFunction(random));
	}

	/**
	 * Returns a new factory.
	 * 
	 * The given random supplier must return an array of 10 bytes.
	 * 
	 * @param randomSupplier a random supplier that returns 10 bytes
	 * @return {@link UlidFactory}
	 */
	public static UlidFactory newInstance(Supplier<byte[]> randomSupplier) {
		return new UlidFactory(new UlidFunction(randomSupplier));
	}

	/**
	 * Returns a new factory.
	 * 
	 * The given random supplier must return an array of 10 bytes.
	 * 
	 * @param randomSupplier a random supplier that returns 10 bytes
	 * @param clock          a custom clock instance for tests
	 * @return {@link UlidFactory}
	 */
	protected static UlidFactory newInstance(Supplier<byte[]> randomSupplier, Clock clock) {
		return new UlidFactory(new UlidFunction(randomSupplier), clock);
	}

	/**
	 * Returns a new monotonic factory.
	 * 
	 * @return {@link UlidFactory}
	 */
	public static UlidFactory newMonotonicInstance() {
		return new UlidFactory(new MonotonicFunction());
	}

	/**
	 * Returns a new monotonic factory.
	 * 
	 * @param random a {@link Random} generator
	 * @return {@link UlidFactory}
	 */
	public static UlidFactory newMonotonicInstance(Random random) {
		return new UlidFactory(new MonotonicFunction(random));
	}

	/**
	 * Returns a new monotonic factory.
	 * 
	 * The given random supplier must return an array of 10 bytes.
	 * 
	 * @param randomSupplier a random supplier that returns 10 bytes
	 * @return {@link UlidFactory}
	 */
	public static UlidFactory newMonotonicInstance(Supplier<byte[]> randomSupplier) {
		return new UlidFactory(new MonotonicFunction(randomSupplier));
	}

	/**
	 * Returns a new monotonic factory.
	 * 
	 * The given random supplier must return an array of 10 bytes.
	 * 
	 * @param randomSupplier a random supplier that returns 10 bytes
	 * @param clock          a custom clock instance for tests
	 * @return {@link UlidFactory}
	 */
	protected static UlidFactory newMonotonicInstance(Supplier<byte[]> randomSupplier, Clock clock) {
		return new UlidFactory(new MonotonicFunction(randomSupplier), clock);
	}

	/**
	 * Returns a UUID.
	 * 
	 * @return a ULID
	 */
	public Ulid create() {
		return create(clock.millis());
	}

	/**
	 * Returns a UUID with a specific time.
	 * 
	 * The time must be the number of milliseconds since 1970-01-01 (Unix epoch).
	 * 
	 * @param time a given time
	 * @return a ULID
	 */
	public Ulid create(final long time) {
		return this.ulidFunction.apply(time);
	}

	/**
	 * Function that creates ULIDs.
	 */
	protected static final class UlidFunction implements LongFunction<Ulid> {

		// it must return an array of 10 bytes
		private Supplier<byte[]> randomSupplier;

		public UlidFunction() {
			this(new SecureRandom());
		}

		public UlidFunction(Random random) {
			this(getRandomSupplier(random));
		}

		public UlidFunction(Supplier<byte[]> randomSupplier) {
			this.randomSupplier = randomSupplier;
		}

		@Override
		public Ulid apply(final long time) {
			return new Ulid(time, this.randomSupplier.get());
		}
	}

	/**
	 * Function that creates Monotonic ULIDs.
	 */
	protected static final class MonotonicFunction implements LongFunction<Ulid> {

		private long lastTime = 0;
		private Ulid lastUlid = null;

		// Used to preserve monotonicity when the system clock is
		// adjusted by NTP after a small clock drift or when the
		// system clock jumps back by 1 second due to leap second.
		protected static final int CLOCK_DRIFT_TOLERANCE = 10_000;

		// it must return an array of 10 bytes
		private Supplier<byte[]> randomSupplier;

		public MonotonicFunction() {
			this(new SecureRandom());
		}

		public MonotonicFunction(Random random) {
			this(getRandomSupplier(random));
		}

		public MonotonicFunction(Supplier<byte[]> randomSupplier) {
			this.randomSupplier = randomSupplier;
		}

		@Override
		public synchronized Ulid apply(final long time) {

			// Check if the current time is the same as the previous time or has moved
			// backwards after a small system clock adjustment or after a leap second.
			// Drift tolerance = (previous_time - 10s) < current_time <= previous_time
			if ((time > this.lastTime - CLOCK_DRIFT_TOLERANCE) && (time <= this.lastTime)) {
				this.lastUlid = lastUlid.increment();
			} else {
				this.lastUlid = new Ulid(time, this.randomSupplier.get());
			}

			this.lastTime = lastUlid.getTime();
			return new Ulid(this.lastUlid);
		}
	}

	/**
	 * It instantiates a supplier that returns an array of 10 bytes.
	 * 
	 * @param random a {@link Random} generator
	 * @return a random supplier that returns 10 bytes
	 */
	protected static Supplier<byte[]> getRandomSupplier(Random random) {
		return () -> {
			byte[] payload = new byte[Ulid.RANDOM_BYTES];
			random.nextBytes(payload);
			return payload;
		};
	}

}
