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

import java.util.UUID;

import com.github.f4b6a3.ulid.exception.UlidCreatorException;
import com.github.f4b6a3.ulid.guid.GuidCreator;

/**
 * A factory for Universally Unique Lexicographically Sortable Identifiers.
 * 
 * See the ULID spec: https://github.com/ulid/spec
 */
public class UlidCreator {

	private UlidCreator() {
	}

	/**
	 * Returns a ULID.
	 * 
	 * @return a ULID
	 */
	public static String getUlid() {
		return GuidCreatorLazyHolder.INSTANCE.createUlid();
	}

	/**
	 * Returns a fast ULID.
	 * 
	 * @return a ULID
	 */
	public static String getFastUlid() {
		return FastGuidCreatorLazyHolder.INSTANCE.createUlid();
	}

	/**
	 * Returns ULID as GUID object.
	 * 
	 * @return a GUID
	 */
	public static UUID getGuid() {
		return GuidCreatorLazyHolder.INSTANCE.create();
	}

	/**
	 * Returns fast ULID as GUID object.
	 * 
	 * @return a GUID
	 */
	public static UUID getFastGuid() {
		return FastGuidCreatorLazyHolder.INSTANCE.create();
	}

	/**
	 * Returns ULID as byte sequence.
	 * 
	 * @return a GUID
	 */
	public static byte[] getBytes() {
		return GuidCreatorLazyHolder.INSTANCE.createBytes();
	}

	/**
	 * Returns fast ULID as byte sequence.
	 * 
	 * @return a GUID
	 */
	public static byte[] getFastBytes() {
		return FastGuidCreatorLazyHolder.INSTANCE.createBytes();
	}

	/**
	 * Return a GUID creator for direct use.
	 * 
	 * This library uses the {@link GuidCreator} internally to generate ULIDs.
	 * 
	 * The {@link GuidCreator} throws a {@link UlidCreatorException} when too
	 * many values are requested in the same millisecond.
	 * 
	 * @return a {@link GuidCreator}
	 */
	public static GuidCreator getGuidCreator() {
		return new GuidCreator();
	}

	private static class GuidCreatorLazyHolder {
		static final GuidCreator INSTANCE = getGuidCreator();
	}

	private static class FastGuidCreatorLazyHolder {
		static final GuidCreator INSTANCE = getGuidCreator().withFastRandomGenerator();
	}
}
