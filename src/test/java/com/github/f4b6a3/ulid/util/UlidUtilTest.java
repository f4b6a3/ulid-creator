package com.github.f4b6a3.ulid.util;

import static org.junit.Assert.*;
import java.time.Instant;
import java.util.UUID;

import org.junit.Test;

import com.github.f4b6a3.ulid.util.UlidUtil.UlidUtilException;
import com.github.f4b6a3.ulid.UlidCreator;

public class UlidUtilTest {

	private static final String EXAMPLE_TIMESTAMP = "0123456789";
	private static final String EXAMPLE_RANDOMNESS = "ABCDEFGHJKMNPQRS";
	private static final String EXAMPLE_ULID = "0123456789ABCDEFGHJKMNPQRS";

	private static final long TIMESTAMP_MAX = 281474976710655l; // 2^48 - 1

	private static final int ULID_LENGTH = 26;
	private static final int DEFAULT_LOOP_MAX = 100_000;

	private static final String[] EXAMPLE_DATES = { "1970-01-01T00:00:00.000Z", "1985-10-26T01:16:00.123Z",
			"2001-09-09T01:46:40.456Z", "2020-01-15T14:30:33.789Z", "2038-01-19T03:14:07.321Z" };

	@Test(expected = UlidUtilException.class)
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

	@Test
	public void testIsValidLoose() {

		String ulid = null; // Null
		assertFalse("Null ULID should be invalid.", UlidUtil.isValid(ulid));

		ulid = ""; // length: 0
		assertFalse("ULID with empty string should be invalid.", UlidUtil.isValid(ulid));

		ulid = EXAMPLE_ULID; // All upper case
		assertTrue("Ulid in upper case should valid.", UlidUtil.isValid(ulid));

		ulid = "0123456789abcdefghjklmnpqr"; // All lower case
		assertTrue("ULID in lower case should be valid.", UlidUtil.isValid(ulid));

		ulid = "0123456789AbCdEfGhJkMnPqRs"; // Mixed case
		assertTrue("Ulid in upper and lower case should valid.", UlidUtil.isValid(ulid));

		ulid = "0123456789ABCDEFGHJKLMNPQ"; // length: 25
		assertFalse("ULID length lower than 26 should be invalid.", UlidUtil.isValid(ulid));

		ulid = "0123456789ABCDEFGHJKMNPQZZZ"; // length: 27
		assertFalse("ULID length greater than 26 should be invalid.", UlidUtil.isValid(ulid));

		ulid = "u123456789ABCDEFGHJKMNPQRS"; // Letter u
		assertFalse("ULID with 'u' or 'U' should be invalid.", UlidUtil.isValid(ulid));

		ulid = "#123456789ABCDEFGHJKMNPQRS"; // Special char
		assertFalse("ULID with special chars should be invalid.", UlidUtil.isValid(ulid));

		ulid = "01234-56789-ABCDEFGHJKMNPQRS"; // Hiphens
		assertTrue("ULID with hiphens should be valid.", UlidUtil.isValid(ulid));

		ulid = "8ZZZZZZZZZABCDEFGHJKMNPQRS"; // timestamp > (2^48)-1
		assertFalse("ULID with timestamp greater than (2^48)-1 should be invalid.", UlidUtil.isValid(ulid));
	}

	@Test
	public void testIsValidStrict() {
		boolean strict = true;

		String ulid = null; // Null
		assertFalse("Null ULID should be invalid in strict mode.", UlidUtil.isValid(ulid, strict));

		ulid = ""; // length: 0
		assertFalse("ULID with empty string should be invalid  in strict mode.", UlidUtil.isValid(ulid, strict));

		ulid = EXAMPLE_ULID; // All upper case
		assertTrue("ULID in upper case should valid in strict mode.", UlidUtil.isValid(ulid, strict));

		ulid = "0123456789abcdefghjkmnpqrs"; // All lower case
		assertTrue("ULID in lower case should be valid in strict mode.", UlidUtil.isValid(ulid, strict));

		ulid = "0123456789AbCdEfGhJkMnPqRs"; // Mixed case
		assertTrue("ULID in upper and lower case should valid in strict mode.", UlidUtil.isValid(ulid, strict));

		ulid = "0123456789ABCDEFGHJKLMNPQ"; // length: 25
		assertFalse("ULID length lower than 26 should be invalid in strict mode.", UlidUtil.isValid(ulid, strict));

		ulid = "0123456789ABCDEFGHJKMNPQZZZ"; // length: 27
		assertFalse("ULID length greater than 26 should be invalid in strict mode.", UlidUtil.isValid(ulid, strict));

		ulid = "i123456789ABCDEFGHJKMNPQRS"; // Letter i
		assertFalse("ULID with 'i' or 'I' should be invalid in strict mode.", UlidUtil.isValid(ulid, strict));

		ulid = "L123456789ABCDEFGHJKMNPQRS"; // letter L
		assertFalse("ULID with 'l' or 'L' should be invalid in strict mode.", UlidUtil.isValid(ulid, strict));

		ulid = "o123456789ABCDEFGHJKMNPQRS"; // letter o
		assertFalse("ULID with 'o' or 'O' should be invalid in strict mode.", UlidUtil.isValid(ulid, strict));

		ulid = "u123456789ABCDEFGHJKMNPQRS"; // letter u
		assertFalse("ULID with 'u' or 'U' should be invalid in strict mode.", UlidUtil.isValid(ulid, strict));

		ulid = "#123456789ABCDEFGHJKMNPQRS"; // Special char
		assertFalse("ULID with special chars should be invalid in strict mode.", UlidUtil.isValid(ulid, strict));

		ulid = "01234-56789-ABCDEFGHJKMNPQRS"; // Hyphens
		assertFalse("ULID with hiphens should be invalid in strict mode.", UlidUtil.isValid(ulid, strict));

		ulid = "8ZZZZZZZZZABCDEFGHJKMNPQRS"; // timestamp > (2^48)-1
		assertFalse("ULID with timestamp greater than (2^48)-1 should be invalid.", UlidUtil.isValid(ulid));
	}

	@Test
	public void testToAndFromUlid() {

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {

			UUID uuid1 = UlidCreator.getGuid();
			String ulid = UlidUtil.fromUuidToUlid(uuid1);

			assertTrue("ULID is null", ulid != null);
			assertTrue("ULID is empty", !ulid.isEmpty());
			assertTrue("ULID length is wrong ", ulid.length() == ULID_LENGTH);
			assertTrue("ULID is not valid", UlidUtil.isValid(ulid, /* strict */
					true));

			UUID uuid2 = UlidUtil.fromUlidToUuid(ulid);
			assertEquals("Result ULID is different from original ULID", uuid1, uuid2);

		}
	}

	@Test
	public void testToAndFromBytes() {
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			String ulid1 = UlidCreator.getUlid();
			byte[] bytes = UlidUtil.fromUlidToBytes(ulid1);
			String ulid2 = UlidUtil.fromBytesToUlid(bytes);

			// Check ULID 1
			assertTrue(ulid1 != null);
			assertTrue(!ulid1.isEmpty());
			assertTrue(ulid1.length() == ULID_LENGTH);
			assertTrue(UlidUtil.isValid(ulid1, /* strict */ true));

			// Check ULID 2
			assertTrue(ulid2 != null);
			assertTrue(!ulid2.isEmpty());
			assertTrue(ulid2.length() == ULID_LENGTH);
			assertTrue(UlidUtil.isValid(ulid2, /* strict */ true));

			assertEquals(ulid1, ulid2);
		}
	}

	@Test
	public void testFromUuidToBytes() {
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			UUID uuid1 = UlidCreator.getGuid();
			byte[] bytes = UlidUtil.fromUuidToBytes(uuid1);
			long msb = ByteUtil.toNumber(ByteUtil.copy(bytes, 0, 8));
			long lsb = ByteUtil.toNumber(ByteUtil.copy(bytes, 8, 16));
			UUID uuid2 = new UUID(msb, lsb);
			assertEquals(uuid1, uuid2);
		}
	}

	@Test
	public void testFromBytesToUuid() {
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			UUID uuid1 = UlidCreator.getGuid();
			byte[] bytes = UlidUtil.fromUuidToBytes(uuid1);
			UUID uuid2 = UlidUtil.fromBytesToUuid(bytes);
			assertEquals(uuid1, uuid2);
		}
	}

	private String leftPad(String unpadded) {
		return "0000000000".substring(unpadded.length()) + unpadded;
	}
}
