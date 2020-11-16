package com.github.f4b6a3.ulid.util.internal;

import static org.junit.Assert.assertEquals;

import java.util.Random;
import java.util.UUID;

import org.junit.Test;

public class UlidStructTest {

	private static final int DEFAULT_LOOP_MAX = 100_000;

	protected static final char[] ALPHABET_CROCKFORD = "0123456789ABCDEFGHJKMNPQRSTVWXYZ".toCharArray();
	protected static final char[] ALPHABET_JAVA = "0123456789abcdefghijklmnopqrstuv".toCharArray(); // Long.parseUnsignedLong()

	@Test
	public void testOfAndToString() {
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			UUID uuid0 = UUID.randomUUID();
			String string0 = toString(uuid0);
			String string1 = UlidStruct.of(string0).toString();
			assertEquals(string0, string1);
		}
		
		// Test RFC-4122 UUID version 4
		final long versionMask = 0xffffffffffff0fffL;
		final long variantMask = 0x3fffffffffffffffL;
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			UUID uuid0 = UUID.randomUUID();
			String string0 = toString(uuid0);
			String string1 = UlidStruct.of(string0).toString4(); // UUID v4 in base32
			UUID uuid1 = toUuid(fromString(string1));
			assertEquals(uuid0.getMostSignificantBits() & versionMask, uuid1.getMostSignificantBits() & versionMask);
			assertEquals(uuid0.getLeastSignificantBits() & variantMask, uuid1.getLeastSignificantBits() & variantMask);
			assertEquals(4, uuid1.version());
			assertEquals(2, uuid1.variant());
		}
	}

	@Test
	public void testOfAndToUuid() {
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			UUID uuid0 = UUID.randomUUID();
			UUID uuid1 = UlidStruct.of(uuid0).toUuid();
			assertEquals(uuid0, uuid1);
		}

		// Test RFC-4122 UUID version 4
		final long versionMask = 0xffffffffffff0fffL;
		final long variantMask = 0x3fffffffffffffffL;
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			UUID uuid0 = UUID.randomUUID();
			UUID uuid1 = UlidStruct.of(uuid0).toUuid4(); // UUID v4
			assertEquals(uuid0.getMostSignificantBits() & versionMask, uuid1.getMostSignificantBits() & versionMask);
			assertEquals(uuid0.getLeastSignificantBits() & variantMask, uuid1.getLeastSignificantBits() & variantMask);
			assertEquals(4, uuid1.version());
			assertEquals(2, uuid1.variant());
		}
	}

	@Test
	public void testConstructorLongs() {
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			Random random = new Random();
			final long time = random.nextLong();
			final long random1 = random.nextLong();
			final long random2 = random.nextLong();
			UlidStruct struct0 = UlidStruct.of(time, random1, random2); // <-- under test

			assertEquals(time & 0xffffffffffffL, struct0.time);
			assertEquals(random1 & 0xffffffffffL, struct0.random1);
			assertEquals(random2 & 0xffffffffffL, struct0.random2);
		}
	}

	@Test
	public void testConstructorString() {
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			Random random = new Random();
			final long time = random.nextLong();
			final long random1 = random.nextLong();
			final long random2 = random.nextLong();
			UlidStruct struct0 = UlidStruct.of(time, random1, random2);

			String string1 = toString(struct0);
			UlidStruct struct1 = UlidStruct.of(string1); // <-- under test
			assertEquals(struct0, struct1);
		}
	}

	@Test
	public void testConstructorUuid() {
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			Random random = new Random();
			final long msb = random.nextLong();
			final long lsb = random.nextLong();
			final UUID uuid0 = new UUID(msb, lsb);
			UlidStruct struct0 = UlidStruct.of(uuid0); // <-- under test

			UUID uuid1 = toUuid(struct0);
			assertEquals(uuid0, uuid1);
		}
	}

	@Test
	public void testToString() {

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			Random random = new Random();
			final long time = random.nextLong();
			final long random1 = random.nextLong();
			final long random2 = random.nextLong();
			UlidStruct struct0 = UlidStruct.of(time, random1, random2);

			String string1 = toString(struct0);
			String string2 = struct0.toString(); // <-- under test
			assertEquals(string1, string2);
		}
	}

	@Test
	public void testToUuid() {

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			Random random = new Random();
			final long time = random.nextLong();
			final long random1 = random.nextLong();
			final long random2 = random.nextLong();
			UlidStruct struct0 = UlidStruct.of(time, random1, random2);

			UUID uuid1 = toUuid(struct0);
			UUID uuid2 = struct0.toUuid(); // <-- under test
			assertEquals(uuid1, uuid2);
		}
	}

	public static UlidStruct fromString(String string) {

		long time = 0;
		long random1 = 0;
		long random2 = 0;

		String tm = string.substring(0, 10);
		String r1 = string.substring(10, 18);
		String r2 = string.substring(18, 26);

		tm = transliterate(tm, ALPHABET_CROCKFORD, ALPHABET_JAVA);
		r1 = transliterate(r1, ALPHABET_CROCKFORD, ALPHABET_JAVA);
		r2 = transliterate(r2, ALPHABET_CROCKFORD, ALPHABET_JAVA);

		time = Long.parseUnsignedLong(tm, 32);
		random1 = Long.parseUnsignedLong(r1, 32);
		random2 = Long.parseUnsignedLong(r2, 32);

		return UlidStruct.of(time, random1, random2);
	}

	public static UUID toUuid(UlidStruct struct) {

		long time = struct.time & 0xffffffffffffL;
		long random1 = struct.random1 & 0xffffffffffL;
		long random2 = struct.random2 & 0xffffffffffL;

		final long msb = (time << 16) | (random1 >>> 24);
		final long lsb = (random1 << 40) | random2;

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

	public static String toString(UlidStruct struct) {
		return toString(struct.time, struct.random1, struct.random2);
	}

	public static String toString(UUID uuid) {
		final long msb = uuid.getMostSignificantBits();
		final long lsb = uuid.getLeastSignificantBits();

		final long time = (msb >>> 16);
		final long random1 = ((msb & 0xffffL) << 24) | (lsb >>> 40);
		final long random2 = (lsb & 0xffffffffffL);

		return toString(time, random1, random2);
	}

	public static String toString(final long time, final long random1, final long random2) {
		String timeComponent = toTimeComponent(time);
		String randomComponent = toRandomComponent(random1, random2);
		return timeComponent + randomComponent;
	}
	
	public static String toTimeComponent(final long time) {
		final String tzero = "0000000000";
		String tm = Long.toUnsignedString(time, 32);
		tm = tzero.substring(0, tzero.length() - tm.length()) + tm;
		return transliterate(tm, ALPHABET_JAVA, ALPHABET_CROCKFORD);
	}
	
	public static String toRandomComponent(final long random1, final long random2) {

		final String zeros = "00000000";

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
