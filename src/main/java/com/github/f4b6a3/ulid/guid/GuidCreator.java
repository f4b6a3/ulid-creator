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

package com.github.f4b6a3.ulid.guid;

import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

import com.github.f4b6a3.ulid.timestamp.TimestampStrategy;
import com.github.f4b6a3.ulid.util.FingerprintUtil;
import com.github.f4b6a3.ulid.util.UlidUtil;
import com.github.f4b6a3.ulid.exception.UlidCreatorException;
import com.github.f4b6a3.ulid.random.Xorshift128PlusRandom;
import com.github.f4b6a3.ulid.random.XorshiftRandom;
import com.github.f4b6a3.ulid.timestamp.DefaultTimestampStrategy;

/**
 * Factory that creates lexicographically sortable GUIDs, based on the ULID
 * specification - Universally Unique Lexicographically Sortable Identifier.
 * 
 * ULID specification: https://github.com/ulid/spec
 */
public class GuidCreator {

	protected static final long MAX_LOW = 0xffffffffffffffffL; // unsigned
	protected static final long MAX_HIGH = 0x000000000000ffffL;

	protected long previousTimestamp;
	protected boolean enableOverflowException = true;

	protected Random random;

	protected long low;
	protected long high;

	protected static final String OVERFLOW_MESSAGE = "The system caused an overflow in the generator by requesting too many IDs.";

	protected TimestampStrategy timestampStrategy;

	public GuidCreator() {
		this.reset();
		this.timestampStrategy = new DefaultTimestampStrategy();
	}

	/**
	 * 
	 * Return a GUID based on the ULID specification.
	 * 
	 * It has two parts:
	 * 
	 * 1. A part of 48 bits that represent the amount of milliseconds since Unix
	 * Epoch, 1 January 1970.
	 * 
	 * 2. A part of 80 bits that has a random value generated a secure random
	 * generator.
	 * 
	 * If more than one GUID is generated within the same millisecond, the
	 * random part is incremented by one.
	 * 
	 * The random part is reset to a new value every time the millisecond part
	 * changes.
	 * 
	 * ### Specification of Universally Unique Lexicographically Sortable ID
	 * 
	 * #### Components
	 * 
	 * ##### Timestamp
	 * 
	 * It is a 48 bit integer. UNIX-time in milliseconds. Won't run out of space
	 * 'til the year 10889 AD.
	 * 
	 * ##### Randomness
	 * 
	 * It is a 80 bits integer. Cryptographically secure source of randomness,
	 * if possible.
	 * 
	 * #### Sorting
	 * 
	 * The left-most character must be sorted first, and the right-most
	 * character sorted last (lexical order). The default ASCII character set
	 * must be used. Within the same millisecond, sort order is not guaranteed.
	 * 
	 * #### Monotonicity
	 * 
	 * When generating a ULID within the same millisecond, we can provide some
	 * guarantees regarding sort order. Namely, if the same millisecond is
	 * detected, the random component is incremented by 1 bit in the least
	 * significant bit position (with carrying).
	 * 
	 * If, in the extremely unlikely event that, you manage to generate more
	 * than 280 ULIDs within the same millisecond, or cause the random component
	 * to overflow with less, the generation will fail.
	 * 
	 * @return {@link UUID} a UUID value
	 * 
	 * @throws UlidCreatorException
	 *             an overflow exception if too many requests within the same
	 *             millisecond causes an overflow when incrementing the random
	 *             bits of the GUID.
	 */
	public synchronized UUID create() {

		final long timestamp = this.getTimestamp();

		final long msb = (timestamp << 16) | high;
		final long lsb = low;

		return new UUID(msb, lsb);
	}
	
	/**
	 * Return a ULID.
	 * 
	 * @return a ULID string
	 */
	public synchronized String createUlid() {
		UUID guid = create();
		return UlidUtil.fromUuidToUlid(guid);
	}
	
	/**
	 * Return a ULID as byte sequence.
	 * 
	 * @return a byte sequence
	 */
	public synchronized byte[] createBytes() {
		UUID guid = create();
		return UlidUtil.fromUuidToBytes(guid);
	}

	/**
	 * Return the current timestamp and resets or increments the random part.
	 * 
	 * @return timestamp
	 */
	protected synchronized long getTimestamp() {

		final long timestamp = this.timestampStrategy.getTimestamp();

		if (timestamp == this.previousTimestamp) {
			this.increment();
		} else {
			this.reset();
		}

		this.previousTimestamp = timestamp;
		return timestamp;
	}

	/**
	 * Reset the random part of the GUID.
	 */
	protected synchronized void reset() {
		if (random == null) {
			this.low = SecureRandomLazyHolder.INSTANCE.nextLong();
			this.high = SecureRandomLazyHolder.INSTANCE.nextLong() & MAX_HIGH;
		} else {
			this.low = random.nextLong();
			this.high = random.nextLong() & MAX_HIGH;
		}
	}

	/**
	 * Increment the random part of the GUID.
	 * 
	 * @throws UlidCreatorException
	 *             if an overflow happens.
	 */
	protected synchronized void increment() {
		if ((this.low++ == MAX_LOW) && (this.high++ == MAX_HIGH)) {
			this.high = 0L;
			// Too many requests
			if (enableOverflowException) {
				throw new UlidCreatorException(OVERFLOW_MESSAGE);
			}
		}
	}

	/**
	 * Used for changing the timestamp strategy.
	 * 
	 * @param timestampStrategy
	 *            a timestamp strategy
	 * @return {@link GuidCreator}
	 */
	@SuppressWarnings("unchecked")
	public synchronized <T extends GuidCreator> T withTimestampStrategy(TimestampStrategy timestampStrategy) {
		this.timestampStrategy = timestampStrategy;
		return (T) this;
	}

	/**
	 * Replace the default random generator, in a fluent way, to another that
	 * extends {@link Random}.
	 * 
	 * The default random generator is {@link java.security.SecureRandom}.
	 * 
	 * For other faster pseudo-random generators, see {@link XorshiftRandom} and
	 * its variations.
	 * 
	 * See {@link Random}.
	 * 
	 * @param random
	 *            a random generator
	 * @return {@link GuidCreator}
	 */
	@SuppressWarnings("unchecked")
	public synchronized <T extends GuidCreator> T withRandomGenerator(Random random) {
		this.random = random;
		return (T) this;
	}

	/**
	 * Replaces the default random generator with a faster one.
	 * 
	 * The host fingerprint is used to generate a seed for the random number
	 * generator.
	 * 
	 * See {@link Xorshift128PlusRandom} and
	 * {@link FingerprintUtil#getFingerprint()}
	 * 
	 * @return {@link GuidCreator}
	 */
	@SuppressWarnings("unchecked")
	public synchronized <T extends GuidCreator> T withFastRandomGenerator() {
		final int salt = (int) FingerprintUtil.getFingerprint();
		this.random = new Xorshift128PlusRandom(salt);
		return (T) this;
	}

	/**
	 * Used to disable the overflow exception.
	 * 
	 * An exception thrown when too many requests within the same millisecond
	 * causes an overflow while incrementing the random bits of the GUID.
	 * 
	 * @return {@link GuidCreator}
	 */
	@SuppressWarnings("unchecked")
	public synchronized <T extends GuidCreator> T withoutOverflowException() {
		this.enableOverflowException = false;
		return (T) this;
	}

	private static class SecureRandomLazyHolder {
		static final Random INSTANCE = new SecureRandom();
	}
}
