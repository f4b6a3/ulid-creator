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

import com.github.f4b6a3.ulid.factory.LexicalOrderGuidCreator;
import com.github.f4b6a3.ulid.util.UlidUtil;

/**
 * A factory for Universally Unique Lexicographically Sortable Identifiers.
 * 
 * @see The ULID spec; https://github.com/ulid/spec
 * 
 */
public class UlidCreator {

	private UlidCreator() {
	}

	public static String getUlid() {
		UUID guid = getLexicalOrderGuid();
		return UlidUtil.fromUuidToUlid(guid);
	}

	/**
	 * Returns a Lexical Order GUID based on the ULID specification.
	 * 
	 * If you need a ULID string instead of a GUID, use
	 * {@link UlidCreator#getUlid()}.
	 * 
	 * @return a Lexical Order GUID
	 */
	public static UUID getLexicalOrderGuid() {
		return LexicalOrderCreatorLazyHolder.INSTANCE.create();
	}

	/**
	 * Returns a {@link LexicalOrderGuidCreator}.
	 * 
	 * @return {@link LexicalOrderGuidCreator}
	 */
	public static LexicalOrderGuidCreator getLexicalOrderCreator() {
		return new LexicalOrderGuidCreator();
	}

	private static class LexicalOrderCreatorLazyHolder {
		static final LexicalOrderGuidCreator INSTANCE = getLexicalOrderCreator().withoutOverflowException();
	}
}
