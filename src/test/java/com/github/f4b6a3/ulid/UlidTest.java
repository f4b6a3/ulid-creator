package com.github.f4b6a3.ulid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

import org.junit.Test;

public class UlidTest extends UlidFactoryTest {

	private static final int DEFAULT_LOOP_MAX = 1_000;

	protected static final long TIME_MASK = 0x0000ffffffffffffL;

	protected static final char[] ALPHABET_CROCKFORD = "0123456789ABCDEFGHJKMNPQRSTVWXYZ".toCharArray();
	protected static final char[] ALPHABET_JAVA = "0123456789abcdefghijklmnopqrstuv".toCharArray(); // Long.parseUnsignedLong()

	private static final long VERSION_MASK = 0x000000000000f000L;
	private static final long VARIANT_MASK = 0xc000000000000000L;

	@Test
	public void testConstructorLongs() {
		Random random = new Random();
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			final long msb = random.nextLong();
			final long lsb = random.nextLong();
			Ulid ulid0 = new Ulid(msb, lsb); // <-- test Ulid(long, long)
			assertEquals(msb, ulid0.getMostSignificantBits());
			assertEquals(lsb, ulid0.getLeastSignificantBits());
		}
	}

	@Test
	public void testConstructorTimeAndRandom() {
		Random random = new Random();

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			final long msb = random.nextLong();
			final long lsb = random.nextLong();

			// get the time
			long time = msb >>> 16;

			// get the random bytes
			ByteBuffer buffer = ByteBuffer.allocate(Ulid.RANDOM_BYTES);
			buffer.put((byte) ((msb >>> 8) & 0xff));
			buffer.put((byte) (msb & 0xff));
			buffer.putLong(lsb);
			byte[] bytes = buffer.array();

			Ulid ulid0 = new Ulid(time, bytes); // <-- test Ulid(long, byte[])
			assertEquals(msb, ulid0.getMostSignificantBits());
			assertEquals(lsb, ulid0.getLeastSignificantBits());
		}

		try {
			long time = 0x0000ffffffffffffL + 1; // greater than 2^48-1
			byte[] bytes = new byte[Ulid.RANDOM_BYTES];
			new Ulid(time, bytes);
			fail("Should throw an exception");
		} catch (IllegalArgumentException e) {
			// success
		}

		try {
			long time = 0x8000000000000000L; // negative number
			byte[] bytes = new byte[Ulid.RANDOM_BYTES];
			new Ulid(time, bytes);
			fail("Should throw an exception");
		} catch (IllegalArgumentException e) {
			// success
		}

		try {
			long time = 0x0000000000000000L;
			byte[] bytes = null; // null random component
			new Ulid(time, bytes);
			fail("Should throw an exception");
		} catch (IllegalArgumentException e) {
			// success
		}

		try {
			long time = 0x0000000000000000L;
			byte[] bytes = new byte[Ulid.RANDOM_BYTES + 1]; // random component with invalid size
			new Ulid(time, bytes);
			fail("Should throw an exception");
		} catch (IllegalArgumentException e) {
			// success
		}
	}

	@Test
	public void testFromStrings() {
		Random random = new Random();
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			final long msb = random.nextLong();
			final long lsb = random.nextLong();
			Ulid ulid0 = new Ulid(msb, lsb);
			String string0 = toString(ulid0);
			Ulid ulid1 = Ulid.from(string0); // <- test Ulid.from(String)
			assertEquals(ulid0, ulid1);
		}
	}

	@Test
	public void testToString() {
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			UUID uuid0 = UUID.randomUUID();
			String string0 = toString(uuid0);
			String string1 = Ulid.from(uuid0).toString(); // <- test Ulid.toString()
			assertEquals(string0, string1);
		}
	}

	@Test
	public void testToUpperCase() {
		Random random = new Random();
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {

			final long msb = random.nextLong();
			final long lsb = random.nextLong();
			Ulid ulid0 = new Ulid(msb, lsb);

			String string1 = toString(ulid0);
			String string2 = ulid0.toString(); // <- test Ulid.toString()
			assertEquals(string1, string2);

			// RFC-4122 UUID v4
			UUID uuid0 = new UUID(msb, lsb);
			String string3 = ulid0.toRfc4122().toString(); // <- test Ulid.toRfc4122().toString()
			Ulid ulid3 = fromString(string3);
			UUID uuid3 = new UUID(ulid3.getMostSignificantBits(), ulid3.getLeastSignificantBits());
			assertEquals(4, uuid3.version()); // check version
			assertEquals(2, uuid3.variant()); // check variant
			assertEquals(uuid0.getMostSignificantBits() & ~VERSION_MASK,
					uuid3.getMostSignificantBits() & ~VERSION_MASK); // check the rest of MSB
			assertEquals(uuid0.getLeastSignificantBits() & ~VARIANT_MASK,
					uuid3.getLeastSignificantBits() & ~VARIANT_MASK); // check the rest of LSB

		}
	}

	@Test
	public void testToLowerCase() {
		Random random = new Random();
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {

			final long msb = random.nextLong();
			final long lsb = random.nextLong();
			Ulid ulid0 = new Ulid(msb, lsb);

			String string1 = toString(ulid0).toLowerCase();
			String string2 = ulid0.toLowerCase(); // <- test Ulid.toLowerCase()
			assertEquals(string1, string2);

			// RFC-4122 UUID v4
			UUID uuid0 = new UUID(msb, lsb);
			String string3 = ulid0.toRfc4122().toLowerCase(); // <- test Ulid.toRfc4122().toLowerCase()
			Ulid ulid3 = fromString(string3);
			UUID uuid3 = new UUID(ulid3.getMostSignificantBits(), ulid3.getLeastSignificantBits());
			assertEquals(4, uuid3.version()); // check version
			assertEquals(2, uuid3.variant()); // check variant
			assertEquals(uuid0.getMostSignificantBits() & ~VERSION_MASK,
					uuid3.getMostSignificantBits() & ~VERSION_MASK); // check the rest of MSB
			assertEquals(uuid0.getLeastSignificantBits() & ~VARIANT_MASK,
					uuid3.getLeastSignificantBits() & ~VARIANT_MASK); // check the rest of LSB
		}
	}

	@Test
	public void testFromUUID() {
		Random random = new Random();
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			final long msb = random.nextLong();
			final long lsb = random.nextLong();
			UUID uuid0 = new UUID(msb, lsb);
			Ulid ulid0 = Ulid.from(uuid0); // <- test Ulid.from(UUID)
			assertEquals(uuid0.getMostSignificantBits(), ulid0.getMostSignificantBits());
			assertEquals(uuid0.getLeastSignificantBits(), ulid0.getLeastSignificantBits());
		}
	}

	@Test
	public void testToUuid() {
		Random random = new Random();
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {

			final long random1 = random.nextLong();
			final long random2 = random.nextLong();
			Ulid ulid0 = new Ulid(random1, random2);

			UUID uuid1 = toUuid(ulid0);
			UUID uuid2 = ulid0.toUuid(); // <-- test Ulid.toUuid()
			assertEquals(uuid1, uuid2);

			// RFC-4122 UUID v4
			UUID uuid3 = ulid0.toRfc4122().toUuid(); // <-- test Ulid.toRfc4122().toUuid()
			assertEquals(4, uuid3.version()); // check version
			assertEquals(2, uuid3.variant()); // check variant
			assertEquals(uuid1.getMostSignificantBits() & ~VERSION_MASK,
					uuid3.getMostSignificantBits() & ~VERSION_MASK); // check the rest of MSB
			assertEquals(uuid1.getLeastSignificantBits() & ~VARIANT_MASK,
					uuid3.getLeastSignificantBits() & ~VARIANT_MASK); // check the rest of LSB
		}
	}

	@Test
	public void testFromBytes() {
		Random random = new Random();
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {

			byte[] bytes0 = new byte[Ulid.ULID_BYTES];
			random.nextBytes(bytes0);

			Ulid ulid0 = Ulid.from(bytes0); // <- test Ulid.from(UUID)
			ByteBuffer buffer = ByteBuffer.allocate(Ulid.ULID_BYTES);
			buffer.putLong(ulid0.getMostSignificantBits());
			buffer.putLong(ulid0.getLeastSignificantBits());
			byte[] bytes1 = buffer.array();

			for (int j = 0; j < bytes0.length; j++) {
				assertEquals(bytes0[j], bytes1[j]);
			}
		}

		try {
			byte[] bytes = null;
			Ulid.from(bytes);
			fail("Should throw an exception");
		} catch (IllegalArgumentException e) {
			// success
		}

		try {
			byte[] bytes = new byte[Ulid.ULID_BYTES + 1];
			Ulid.from(bytes);
			fail("Should throw an exception");
		} catch (IllegalArgumentException e) {
			// success
		}
	}

	@Test
	public void testToBytes() {
		Random random = new Random();
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {

			byte[] bytes1 = new byte[16];
			random.nextBytes(bytes1);
			Ulid ulid0 = Ulid.from(bytes1);

			byte[] bytes2 = ulid0.toBytes(); // <-- test Ulid.toBytes()
			for (int j = 0; j < bytes1.length; j++) {
				assertEquals(bytes1[j], bytes2[j]);
			}

			// RFC-4122 UUID v4
			byte[] bytes3 = ulid0.toRfc4122().toBytes(); // <-- test Ulid.toBytes4()
			assertEquals(0x40, bytes3[6] & 0b11110000); // check version
			assertEquals(bytes1[6] & 0b00001111, bytes3[6] & 0b00001111); // check the other bits of 7th byte
			assertEquals(0x80, bytes3[8] & 0b11000000); // check variant
			assertEquals(bytes1[8] & 0b00111111, bytes3[8] & 0b00111111); // check the other bits of 9th byte
			for (int j = 0; j < bytes1.length; j++) {
				if (j == 6 || j == 8)
					continue;
				assertEquals(bytes1[j], bytes3[j]); // check the other bytes
			}
		}
	}

	@Test
	public void testGetTimeAndGetRandom() {

		long time = 0;
		byte[] bytes = new byte[Ulid.RANDOM_BYTES];
		Random random = new Random();

		for (int i = 0; i < 100; i++) {

			time = random.nextLong() & TIME_MASK;
			random.nextBytes(bytes);

			// Instance methods
			Ulid ulid = new Ulid(time, bytes);
			assertEquals(time, ulid.getTime()); // test Ulid.getTime()
			assertEquals(Instant.ofEpochMilli(time), ulid.getInstant()); // test Ulid.getInstant()
			for (int j = 0; j < bytes.length; j++) {
				assertEquals(bytes[j], ulid.getRandom()[j]); // test Ulid.getRandom()
			}

			// Static methods
			String string = new Ulid(time, bytes).toString();
			assertEquals(time, Ulid.getTime(string)); // test Ulid.getTime()
			assertEquals(Instant.ofEpochMilli(time), Ulid.getInstant(string)); // test Ulid.getInstant()
			for (int j = 0; j < bytes.length; j++) {
				assertEquals(bytes[j], Ulid.getRandom(string)[j]); // test Ulid.getRandom()
			}
		}
	}

	@Test
	public void testIncrement() {

		final long milliseconds = System.currentTimeMillis();
		final BigInteger increment = BigInteger.valueOf(DEFAULT_LOOP_MAX);

		// Test 1
		byte[] random1 = { //
				(byte) 0x00, (byte) 0x11, (byte) 0x22, (byte) 0x33, (byte) 0x44, //
				(byte) 0x55, (byte) 0x66, (byte) 0x77, (byte) 0x88, (byte) 0x99 };
		Ulid ulid1 = new Ulid(milliseconds, random1);
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			ulid1 = ulid1.increment();
		}
		assertEquals(milliseconds, ulid1.getTime());
		assertEquals(new BigInteger(random1).add(increment), new BigInteger(ulid1.getRandom()));

		// Test 2
		byte[] random2 = { //
				(byte) 0x00, (byte) 0x11, (byte) 0xff, (byte) 0xff, (byte) 0xff, //
				(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff };
		Ulid ulid2 = new Ulid(milliseconds, random2);
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			ulid2 = ulid2.increment();
		}
		assertEquals(milliseconds, ulid2.getTime());
		assertEquals(new BigInteger(random2).add(increment), new BigInteger(ulid2.getRandom()));
	}

	@Test
	public void testHashCode() {

		Random random = new Random();
		byte[] bytes = new byte[Ulid.ULID_BYTES];

		// invoked on the same object
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			random.nextBytes(bytes);
			Ulid ulid1 = Ulid.from(bytes);
			assertEquals(ulid1.hashCode(), ulid1.hashCode());
		}

		// invoked on two equal objects
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			random.nextBytes(bytes);
			Ulid ulid1 = Ulid.from(bytes);
			Ulid ulid2 = Ulid.from(bytes);
			assertEquals(ulid1.hashCode(), ulid2.hashCode());
		}
	}

	@Test
	public void testEquals() {

		Random random = new Random();
		byte[] bytes = new byte[Ulid.ULID_BYTES];

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {

			random.nextBytes(bytes);
			Ulid ulid1 = Ulid.from(bytes);
			Ulid ulid2 = Ulid.from(bytes);
			assertEquals(ulid1, ulid2);
			assertEquals(ulid1.toString(), ulid2.toString());
			assertEquals(Arrays.toString(ulid1.toBytes()), Arrays.toString(ulid2.toBytes()));

			// change all bytes
			for (int j = 0; j < bytes.length; j++) {
				bytes[j]++;
			}
			Ulid ulid3 = Ulid.from(bytes);
			assertNotEquals(ulid1, ulid3);
			assertNotEquals(ulid1.toString(), ulid3.toString());
			assertNotEquals(Arrays.toString(ulid1.toBytes()), Arrays.toString(ulid3.toBytes()));
		}
	}

	@Test
	public void testCompareTo() {

		final long zero = 0L;
		Random random = new Random();
		byte[] bytes = new byte[Ulid.ULID_BYTES];

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {

			bytes = ByteBuffer.allocate(16).putLong(random.nextLong()).putLong(random.nextLong()).array();
			Ulid ulid1 = Ulid.from(bytes);
			BigInteger number1 = new BigInteger(1, bytes);

			bytes = ByteBuffer.allocate(16).putLong(random.nextLong()).putLong(random.nextLong()).array();
			Ulid ulid2 = Ulid.from(bytes);
			Ulid ulid3 = Ulid.from(bytes);
			BigInteger number2 = new BigInteger(1, bytes);
			BigInteger number3 = new BigInteger(1, bytes);

			// compare numerically
			assertEquals(number1.compareTo(number2) > 0, ulid1.compareTo(ulid2) > 0);
			assertEquals(number1.compareTo(number2) < 0, ulid1.compareTo(ulid2) < 0);
			assertEquals(number2.compareTo(number3) == 0, ulid2.compareTo(ulid3) == 0);

			// compare lexicographically
			assertEquals(number1.compareTo(number2) > 0, ulid1.toString().compareTo(ulid2.toString()) > 0);
			assertEquals(number1.compareTo(number2) < 0, ulid1.toString().compareTo(ulid2.toString()) < 0);
			assertEquals(number2.compareTo(number3) == 0, ulid2.toString().compareTo(ulid3.toString()) == 0);
		}

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {

			bytes = ByteBuffer.allocate(16).putLong(zero).putLong(random.nextLong()).array();
			Ulid ulid1 = Ulid.from(bytes);
			BigInteger number1 = new BigInteger(1, bytes);

			bytes = ByteBuffer.allocate(16).putLong(zero).putLong(random.nextLong()).array();
			Ulid ulid2 = Ulid.from(bytes);
			Ulid ulid3 = Ulid.from(bytes);
			BigInteger number2 = new BigInteger(1, bytes);
			BigInteger number3 = new BigInteger(1, bytes);

			// compare numerically
			assertEquals(number1.compareTo(number2) > 0, ulid1.compareTo(ulid2) > 0);
			assertEquals(number1.compareTo(number2) < 0, ulid1.compareTo(ulid2) < 0);
			assertEquals(number2.compareTo(number3) == 0, ulid2.compareTo(ulid3) == 0);

			// compare lexicographically
			assertEquals(number1.compareTo(number2) > 0, ulid1.toString().compareTo(ulid2.toString()) > 0);
			assertEquals(number1.compareTo(number2) < 0, ulid1.toString().compareTo(ulid2.toString()) < 0);
			assertEquals(number2.compareTo(number3) == 0, ulid2.toString().compareTo(ulid3.toString()) == 0);
		}

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {

			bytes = ByteBuffer.allocate(16).putLong(random.nextLong()).putLong(zero).array();
			Ulid ulid1 = Ulid.from(bytes);
			BigInteger number1 = new BigInteger(1, bytes);

			bytes = ByteBuffer.allocate(16).putLong(random.nextLong()).putLong(zero).array();
			Ulid ulid2 = Ulid.from(bytes);
			Ulid ulid3 = Ulid.from(bytes);
			BigInteger number2 = new BigInteger(1, bytes);
			BigInteger number3 = new BigInteger(1, bytes);

			// compare numerically
			assertEquals(number1.compareTo(number2) > 0, ulid1.compareTo(ulid2) > 0);
			assertEquals(number1.compareTo(number2) < 0, ulid1.compareTo(ulid2) < 0);
			assertEquals(number2.compareTo(number3) == 0, ulid2.compareTo(ulid3) == 0);

			// compare lexicographically
			assertEquals(number1.compareTo(number2) > 0, ulid1.toString().compareTo(ulid2.toString()) > 0);
			assertEquals(number1.compareTo(number2) < 0, ulid1.toString().compareTo(ulid2.toString()) < 0);
			assertEquals(number2.compareTo(number3) == 0, ulid2.toString().compareTo(ulid3.toString()) == 0);
		}
	}

	@Test
	public void testIsValidString() {

		String ulid = null; // Null
		assertFalse("Null ULID should be invalid.", Ulid.isValid(ulid));

		ulid = ""; // length: 0
		assertFalse("ULID with empty string should be invalid .", Ulid.isValid(ulid));

		ulid = "0123456789ABCDEFGHJKMNPQRS"; // All upper case
		assertTrue("ULID in upper case should valid.", Ulid.isValid(ulid));

		ulid = "0123456789abcdefghjklmnpqr"; // All lower case
		assertTrue("ULID in lower case should be valid.", Ulid.isValid(ulid));

		ulid = "0123456789AbCdEfGhJkMnPqRs"; // Mixed case
		assertTrue("Ulid in upper and lower case should valid.", Ulid.isValid(ulid));

		ulid = "0123456789ABCDEFGHJKLMNPQ"; // length: 25
		assertFalse("ULID length lower than 26 should be invalid.", Ulid.isValid(ulid));

		ulid = "0123456789ABCDEFGHJKMNPQZZZ"; // length: 27
		assertFalse("ULID length greater than 26 should be invalid.", Ulid.isValid(ulid));

		ulid = "u123456789ABCDEFGHJKMNPQRS"; // Letter u
		assertFalse("ULID with 'u' or 'U' should be invalid.", Ulid.isValid(ulid));

		ulid = "0123456789ABCDEFGHJKMNPQR#"; // Special char
		assertFalse("ULID with special chars should be invalid.", Ulid.isValid(ulid));

		ulid = "8ZZZZZZZZZABCDEFGHJKMNPQRS"; // time > (2^48)-1
		assertFalse("ULID with timestamp greater than (2^48)-1 should be invalid.", Ulid.isValid(ulid));
	}

	@Test
	public void testToCharArray() {

		String ulid = null; // Null
		try {
			Ulid.toCharArray(ulid);
			fail("Null ULID should be invalid.");
		} catch (IllegalArgumentException e) {
			// success
		}

		ulid = ""; // length: 0
		try {
			Ulid.toCharArray(ulid);
			fail("Should throw an exception");
		} catch (IllegalArgumentException e) {
			// success
		}

		ulid = "0123456789ABCDEFGHJKMNPQRS"; // All upper case
		try {
			Ulid.toCharArray(ulid);
		} catch (IllegalArgumentException e) {
			fail("Should not throw an exception");
		}

		ulid = "0123456789abcdefghjklmnpqr"; // All lower case
		try {
			Ulid.toCharArray(ulid);
		} catch (IllegalArgumentException e) {
			fail("Should not throw an exception");
		}

		ulid = "0123456789AbCdEfGhJkMnPqRs"; // Mixed case
		try {
			Ulid.toCharArray(ulid);
		} catch (IllegalArgumentException e) {
			fail("Should not throw an exception");
		}

		ulid = "0123456789ABCDEFGHJKLMNPQ"; // length: 25
		try {
			Ulid.toCharArray(ulid);
			fail("Should throw an exception");
		} catch (IllegalArgumentException e) {
			// success
		}

		ulid = "0123456789ABCDEFGHJKMNPQZZZ"; // length: 27
		try {
			Ulid.toCharArray(ulid);
			fail("Should throw an exception");
		} catch (IllegalArgumentException e) {
			// success
		}

		ulid = "u123456789ABCDEFGHJKMNPQRS"; // Letter u
		try {
			Ulid.toCharArray(ulid);
			fail("Should throw an exception");
		} catch (IllegalArgumentException e) {
			// success
		}

		ulid = "0123456789ABCDEFGHJKMNPQR@"; // Special char
		try {
			Ulid.toCharArray(ulid);
			fail("Should throw an exception");
		} catch (IllegalArgumentException e) {
			// success
		}

		ulid = "8ZZZZZZZZZABCDEFGHJKMNPQRS"; // time > (2^48)-1
		try {
			Ulid.toCharArray(ulid);
			fail("Should throw an exception");
		} catch (IllegalArgumentException e) {
			// success
		}
	}

	@Test
	public void testUlidFast() {
		Ulid[] list = new Ulid[DEFAULT_LOOP_MAX];

		long startTime = System.currentTimeMillis();

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = Ulid.fast();
		}

		long endTime = System.currentTimeMillis();

		checkNullOrInvalid(list);
		checkUniqueness(list);
		checkCreationTime(list, startTime, endTime);
	}

	public static Ulid fromString(String string) {

		long time = 0;
		long random1 = 0;
		long random2 = 0;

		String tm = string.substring(0, 10).toUpperCase();
		String r1 = string.substring(10, 18).toUpperCase();
		String r2 = string.substring(18, 26).toUpperCase();

		tm = transliterate(tm, ALPHABET_CROCKFORD, ALPHABET_JAVA);
		r1 = transliterate(r1, ALPHABET_CROCKFORD, ALPHABET_JAVA);
		r2 = transliterate(r2, ALPHABET_CROCKFORD, ALPHABET_JAVA);

		time = Long.parseUnsignedLong(tm, 32);
		random1 = Long.parseUnsignedLong(r1, 32);
		random2 = Long.parseUnsignedLong(r2, 32);

		long msb = (time << 16) | (random1 >>> 24);
		long lsb = (random1 << 40) | (random2 & 0xffffffffffL);

		return new Ulid(msb, lsb);
	}

	public static UUID toUuid(Ulid struct) {

		long msb = struct.toUuid().getMostSignificantBits();
		long lsb = struct.toUuid().getLeastSignificantBits();

		return new UUID(msb, lsb);
	}

	public static UUID toUuid(final long time, final long random1, final long random2) {

		long tm = time & 0xffffffffffffL;
		long r1 = random1 & 0xffffffffffL;
		long r2 = random2 & 0xffffffffffL;

		final long msb = (tm << 16) | (r1 >>> 24);
		final long lsb = (r1 << 40) | r2;

		return new UUID(msb, lsb);
	}

	public static String toString(Ulid ulid) {
		return toString(ulid.toUuid().getMostSignificantBits(), ulid.toUuid().getLeastSignificantBits());
	}

	public static String toString(UUID uuid) {
		final long msb = uuid.getMostSignificantBits();
		final long lsb = uuid.getLeastSignificantBits();
		return toString(msb, lsb);
	}

	public static String toString(final long msb, final long lsb) {
		String timeComponent = toTimeComponent(msb >>> 16);
		String randomComponent = toRandomComponent(msb, lsb);
		return timeComponent + randomComponent;
	}

	public static String toTimeComponent(final long time) {
		final String tzero = "0000000000";
		String tm = Long.toUnsignedString(time, 32);
		tm = tzero.substring(0, tzero.length() - tm.length()) + tm;
		return transliterate(tm, ALPHABET_JAVA, ALPHABET_CROCKFORD);
	}

	public static String toRandomComponent(final long msb, final long lsb) {

		final String zeros = "00000000";

		final long random1 = ((msb & 0xffffL) << 24) | (lsb >>> 40);
		final long random2 = (lsb & 0xffffffffffL);

		String r1 = Long.toUnsignedString(random1, 32);
		String r2 = Long.toUnsignedString(random2, 32);

		r1 = zeros.substring(0, zeros.length() - r1.length()) + r1;
		r2 = zeros.substring(0, zeros.length() - r2.length()) + r2;

		r1 = transliterate(r1, ALPHABET_JAVA, ALPHABET_CROCKFORD);
		r2 = transliterate(r2, ALPHABET_JAVA, ALPHABET_CROCKFORD);

		return r1 + r2;
	}

	private static String transliterate(String string, char[] alphabet1, char[] alphabet2) {
		char[] output = string.toCharArray();
		for (int i = 0; i < output.length; i++) {
			for (int j = 0; j < alphabet1.length; j++) {
				if (output[i] == alphabet1[j]) {
					output[i] = alphabet2[j];
					break;
				}
			}
		}
		return new String(output);
	}
}
