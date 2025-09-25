/*
 * MIT License
 * 
 * Copyright (c) 2020-2023 Fabio Lima
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

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * A class that generates ULIDs.
 * <p>
 * Both types of ULID can be easily created by this generator, i.e. monotonic
 * and non-monotonic.
 * <p>
 * In addition, a "non-standard" hash-based ULID can also be generated, in which
 * the random component is replaced with the first 10 bytes of an SHA-256 hash.
 */
public final class UlidCreator {

	private UlidCreator() {
	}

	/**
	 * Returns a ULID.
	 * <p>
	 * The random component is reset for each new ULID generated.
	 * 
	 * @return a ULID
	 */
	public static Ulid getUlid() {
		return UlidFactoryHolder.instance().create();
	}

	/**
	 * Returns a ULID.
	 * <p>
	 * The random component is reset for each new ULID generated.
	 * 
	 * @param time the current time in milliseconds, measured from the UNIX epoch of
	 *             1970-01-01T00:00Z (UTC)
	 * @return a ULID
	 */
	public static Ulid getUlid(final long time) {
		return UlidFactoryHolder.instance().create(time);
	}

	/**
	 * Returns a Monotonic ULID.
	 * <p>
	 * The random component is incremented for each new ULID generated in the same
	 * millisecond.
	 * 
	 * @return a ULID
	 */
	public static Ulid getMonotonicUlid() {
		return MonotonicFactoryHolder.instance().create();
	}

	/**
	 * Returns a Monotonic ULID.
	 * <p>
	 * The random component is incremented for each new ULID generated in the same
	 * millisecond.
	 * 
	 * @param time the current time in milliseconds, measured from the UNIX epoch of
	 *             1970-01-01T00:00Z (UTC)
	 * @return a ULID
	 */
	public static Ulid getMonotonicUlid(final long time) {
		return MonotonicFactoryHolder.instance().create(time);
	}

	/**
	 * Returns a Hash ULID.
	 * <p>
	 * The random component is replaced with the first 10 bytes of an SHA-256 hash.
	 * <p>
	 * It always returns the same ULID for a specific pair of {@code time} and
	 * {@code string}.
	 * <p>
	 * Usage example:
	 * 
	 * <pre>{@code
	 * long time = file.getCreatedAt();
	 * String name = file.getFileName();
	 * Ulid ulid = UlidCreator.getHashUlid(time, name);
	 * }</pre>
	 * 
	 * @param time   the time in milliseconds, measured from the UNIX epoch of
	 *               1970-01-01T00:00Z (UTC)
	 * @param string a string to be hashed using SHA-256 algorithm.
	 * @return a ULID
	 * @since 5.2.0
	 */
	public static Ulid getHashUlid(final long time, String string) {
		byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
		return getHashUlid(time, bytes);
	}

	/**
	 * Returns a Hash ULID.
	 * <p>
	 * The random component is replaced with the first 10 bytes of an SHA-256 hash.
	 * <p>
	 * It always returns the same ULID for a specific pair of {@code time} and
	 * {@code bytes}.
	 * <p>
	 * Usage example:
	 * 
	 * <pre>{@code
	 * long time = file.getCreatedAt();
	 * byte[] bytes = file.getFileBinary();
	 * Ulid ulid = UlidCreator.getHashUlid(time, bytes);
	 * }</pre>
	 * 
	 * @param time  the time in milliseconds, measured from the UNIX epoch of
	 *              1970-01-01T00:00Z (UTC)
	 * @param bytes a byte array to be hashed using SHA-256 algorithm.
	 * @return a ULID
	 * @since 5.2.0
	 */
	public static Ulid getHashUlid(final long time, byte[] bytes) {
		// Calculate the hash and take the first 10 bytes
		byte[] hash = hasher("SHA-256").digest(bytes);
		byte[] rand = Arrays.copyOf(hash, 10);
		return new Ulid(time, rand);
	}

	private static MessageDigest hasher(final String algorithm) {
		try {
			return MessageDigest.getInstance(algorithm);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(String.format("%s not supported", algorithm));
		}
	}

	private static class UlidFactoryHolder {
		static UlidFactory instance;

        static UlidFactory instance() {
            if (instance == null) {
                instance = UlidFactory.newInstance();
            }
            return instance;
        }
	}

	private static class MonotonicFactoryHolder {
        static UlidFactory instance;

        static UlidFactory instance() {
            if (instance == null) {
                instance = UlidFactory.newMonotonicInstance();
            }
            return instance;
        }
	}
}
