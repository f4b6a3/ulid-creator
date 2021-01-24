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

import com.github.f4b6a3.ulid.creator.UlidSpecCreator;
import com.github.f4b6a3.ulid.creator.impl.DefaultUlidSpecCreator;
import com.github.f4b6a3.ulid.creator.impl.MonotonicUlidSpecCreator;

public final class UlidCreator {

	private UlidCreator() {
	}

	public static Ulid getUlid() {
		return DefaultCreatorHolder.INSTANCE.create();
	}

	public static Ulid getUlid(final long time) {
		return DefaultCreatorHolder.INSTANCE.create(time);
	}

	public static Ulid getMonotonicUlid() {
		return MonotonicCreatorHolder.INSTANCE.create();
	}

	public static Ulid getMonotonicUlid(final long time) {
		return MonotonicCreatorHolder.INSTANCE.create(time);
	}

	public static DefaultUlidSpecCreator getDefaultCreator() {
		return new DefaultUlidSpecCreator();
	}

	public static MonotonicUlidSpecCreator getMonotonicCreator() {
		return new MonotonicUlidSpecCreator();
	}

	private static class DefaultCreatorHolder {
		static final UlidSpecCreator INSTANCE = getDefaultCreator();
	}

	private static class MonotonicCreatorHolder {
		static final UlidSpecCreator INSTANCE = getMonotonicCreator();
	}
}
