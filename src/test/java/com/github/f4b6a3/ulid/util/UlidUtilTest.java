package com.github.f4b6a3.ulid.util;

import static org.junit.Assert.*;
import java.time.Instant;

import org.junit.Test;

import com.github.f4b6a3.util.Base32Util;
import com.github.f4b6a3.util.ByteUtil;
import com.github.f4b6a3.ulid.exception.InvalidUlidException;
import com.github.f4b6a3.ulid.util.UlidUtil;

public class UlidUtilTest {

	private static final String EXAMPLE_TIMESTAMP = "0123456789";
	private static final String EXAMPLE_RANDOMNESS = "ABCDEFGHJKMNPQRS";
	private static final String EXAMPLE_ULID = "0123456789ABCDEFGHJKMNPQRS";

	private static final long TIMESTAMP_MAX = 281474976710655l; // 2^48 - 1

	private static final String[] EXAMPLE_DATES = { "1970-01-01T00:00:00.000Z", "1985-10-26T01:16:00.123Z",
			"2001-09-09T01:46:40.456Z", "2020-01-15T14:30:33.789Z", "2038-01-19T03:14:07.321Z" };

	@Test(expected = InvalidUlidException.class)
	public void testExtractTimestamp() {

		String ulid = "0000000000" + EXAMPLE_RANDOMNESS;
		long milliseconds = UlidUtil.extractTimestamp(ulid);
		assertEquals(0, milliseconds);

		ulid = "7ZZZZZZZZZ" + EXAMPLE_RANDOMNESS;
		milliseconds = UlidUtil.extractTimestamp(ulid);
		assertEquals(TIMESTAMP_MAX, milliseconds);

		ulid = "8ZZZZZZZZZ" + EXAMPLE_RANDOMNESS;
		UlidUtil.extractTimestamp(ulid);
		fail("Should throw exception: invalid ULID");
	}

	@Test
	public void testExtractTimestampList() {

		String randomnessComponent = EXAMPLE_RANDOMNESS;

		for (String i : EXAMPLE_DATES) {

			long milliseconds = Instant.parse(i).toEpochMilli();

			String timestampComponent = leftPad(Base32Util.toBase32Crockford(milliseconds));
			String ulid = timestampComponent + randomnessComponent;
			long result = UlidUtil.extractTimestamp(ulid);

			assertEquals(milliseconds, result);
		}
	}

	@Test
	public void testExtractInstant() {

		String randomnessComponent = EXAMPLE_RANDOMNESS;

		for (String i : EXAMPLE_DATES) {

			Instant instant = Instant.parse(i);
			long milliseconds = Instant.parse(i).toEpochMilli();

			byte[] bytes = new byte[6];
			System.arraycopy(ByteUtil.toBytes(milliseconds), 2, bytes, 0, 6);

			String timestampComponent = leftPad(Base32Util.toBase32Crockford(milliseconds));
			String ulid = timestampComponent + randomnessComponent;
			Instant result = UlidUtil.extractInstant(ulid);

			assertEquals(instant, result);
		}
	}

	@Test
	public void testExtractTimestampComponent() {
		String ulid = EXAMPLE_ULID;
		String expected = EXAMPLE_TIMESTAMP;
		String result = UlidUtil.extractTimestampComponent(ulid);
		assertEquals(expected, result);
	}

	@Test
	public void testExtractRandomnessComponent() {
		String ulid = EXAMPLE_ULID;
		String expected = EXAMPLE_RANDOMNESS;
		String result = UlidUtil.extractRandomnessComponent(ulid);
		assertEquals(expected, result);
	}

	private String leftPad(String unpadded) {
		return "0000000000".substring(unpadded.length()) + unpadded;
	}
}
