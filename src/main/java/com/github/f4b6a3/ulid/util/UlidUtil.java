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

package com.github.f4b6a3.ulid.util;

import java.time.Instant;
import java.util.UUID;

public class UlidUtil {

	// Date: 10889-08-02T05:31:50.655Z
	protected static final long TIMESTAMP_MAX = (long) Math.pow(2, 48) - 1;

	protected static final String ULID_PATTERN_STRICT = "^[0-9a-hjkmnp-tv-zA-HJKMNP-TV-Z]{26}$";
	protected static final String ULID_PATTERN_LOOSE = "^[0-9a-tv-zA-TV-Z]{26}$";

	private UlidUtil() {
	}

	/**
	 * Convert a UUID to ULID string
	 * 
	 * @param uuid
	 *            a UUID
	 * @return a ULID
	 */
	public static String fromUuidToUlid(UUID uuid) {
		byte[] bytes = fromUuidToBytes(uuid);
		return fromBytesToUlid(bytes);
	}

	/**
	 * Converts a ULID string to a UUID.
	 * 
	 * An exception is thrown if the ULID string is invalid.
	 * 
	 * @param ulid
	 *            a ULID
	 * @return a UUID if valid
	 */
	public static UUID fromUlidToUuid(String ulid) {
		byte[] bytes = fromUlidToBytes(ulid);
		return fromBytesToUuid(bytes);
	}

	/**
	 * Get the array of bytes from a UUID.
	 * 
	 * @param uuid
	 *            a UUID
	 * @return an array of bytes
	 */
	public static byte[] fromUuidToBytes(UUID uuid) {
		long msb = uuid.getMostSignificantBits();
		long lsb = uuid.getLeastSignificantBits();
		byte[] msbBytes = ByteUtil.toBytes(msb);
		byte[] lsbBytes = ByteUtil.toBytes(lsb);
		return ByteUtil.concat(msbBytes, lsbBytes);
	}

	/**
	 * Get a UUID from an array of bytes;
	 * 
	 * @param bytes
	 *            an array of bytes
	 * @return a UUID
	 */
	public static UUID fromBytesToUuid(byte[] bytes) {
		byte[] msbBytes = ByteUtil.copy(bytes, 0, 8);
		byte[] lsbBytes = ByteUtil.copy(bytes, 8, 16);
		long msb = ByteUtil.toNumber(msbBytes);
		long lsb = ByteUtil.toNumber(lsbBytes);
		return new UUID(msb, lsb);
	}

	/**
	 * Convert an array of bytes to a ULID string.
	 * 
	 * @param bytes
	 *            a byte array
	 * @return a ULID string
	 */
	public static String fromBytesToUlid(byte[] bytes) {

		byte[] timeBytes = new byte[6];
		System.arraycopy(bytes, 0, timeBytes, 0, 6);
		long timeNumber = ByteUtil.toNumber(timeBytes);
		String timestampComponent = leftPad(Base32Util.toBase32Crockford(timeNumber));

		byte[] randBytes = new byte[10];
		System.arraycopy(bytes, 6, randBytes, 0, 10);
		String randomnessComponent = Base32Util.toBase32Crockford(randBytes);

		return timestampComponent + randomnessComponent;
	}

	/**
	 * Convert a ULID string to an array of bytes.
	 * 
	 * @param ulid
	 *            a ULID string
	 * @return an array of bytes
	 */
	public static byte[] fromUlidToBytes(String ulid) {
		UlidUtil.validate(ulid);
		byte[] bytes = new byte[16];

		String timestampComponent = ulid.substring(0, 10);
		long timeNumber = Base32Util.fromBase32CrockfordAsLong(timestampComponent);
		byte[] timeBytes = ByteUtil.toBytes(timeNumber);
		System.arraycopy(timeBytes, 2, bytes, 0, 6);

		String randomnessComponent = ulid.substring(10, 26);
		byte[] randBytes = Base32Util.fromBase32Crockford(randomnessComponent);
		System.arraycopy(randBytes, 0, bytes, 6, 10);

		return bytes;
	}

	/**
	 * Checks if the ULID string is a valid.
	 * 
	 * The validation mode is not strict.
	 * 
	 * See {@link UlidUtil#validate(String, boolean)}.
	 * 
	 * @param ulid
	 *            a ULID
	 */
	protected static void validate(String ulid) {
		validate(ulid, false);
	}

	/**
	 * Checks if the ULID string is a valid.
	 * 
	 * See {@link UlidUtil#validate(String, boolean)}.
	 * 
	 * @param ulid
	 *            a ULID
	 */
	protected static void validate(String ulid, boolean strict) {
		if (!isValid(ulid, strict)) {
			throw new UlidUtilException(String.format("Invalid ULID: %s.", ulid));
		}
	}

	/**
	 * Checks if the string is a valid ULID.
	 * 
	 * The validation mode is not strict.
	 * 
	 * See {@link UlidUtil#validate(String, boolean)}.
	 */
	public static boolean isValid(String ulid) {
		return isValid(ulid, false);
	}

	/**
	 * Checks if the string is a valid ULID.
	 * 
	 * <pre>
	 * Strict validation: checks if the string is in the ULID specification format:
	 * 
	 * - 0123456789ABCDEFGHJKMNPKRS (26 alphanumeric, case insensitive, except iI, lL, oO and uU)
	 * 
	 * Loose validation: checks if the string is in one of these formats:
	 *
	 * - 0123456789ABCDEFGHIJKLMNOP (26 alphanumeric, case insensitive, except uU)
	 * </pre>
	 * 
	 * @param ulid
	 *            a ULID
	 * @param strict
	 *            true for strict validation, false for loose validation
	 * @return boolean true if valid
	 */
	public static boolean isValid(String ulid, boolean strict) {

		if (ulid == null || ulid.isEmpty()) {
			return false;
		}

		boolean matches = false;

		if (strict) {
			matches = ulid.matches(ULID_PATTERN_STRICT);
		} else {
			String u = ulid.replaceAll("-", "");
			matches = u.matches(ULID_PATTERN_LOOSE);
		}

		if (!matches) {
			return false;
		}

		long timestamp = extractUnixMilliseconds(ulid);
		return timestamp >= 0 && timestamp <= TIMESTAMP_MAX;
	}

	public static long extractTimestamp(String ulid) {
		UlidUtil.validate(ulid);
		return extractUnixMilliseconds(ulid);
	}

	public static Instant extractInstant(String ulid) {
		long milliseconds = extractTimestamp(ulid);
		return Instant.ofEpochMilli(milliseconds);
	}

	public static String extractTimestampComponent(String ulid) {
		UlidUtil.validate(ulid);
		return ulid.substring(0, 10);
	}

	public static String extractRandomnessComponent(String ulid) {
		UlidUtil.validate(ulid);
		return ulid.substring(10, 26);
	}

	protected static long extractUnixMilliseconds(String ulid) {
		String milliseconds = ulid.substring(0, 10);
		return Base32Util.fromBase32CrockfordAsLong(milliseconds);
	}

	private static String leftPad(String unpadded) {
		return "0000000000".substring(unpadded.length()) + unpadded;
	}

	public static class UlidUtilException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public UlidUtilException(String message) {
			super(message);
		}
	}
}
