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
	public void testConstructorLongs() {
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			Random random = new Random();
			final long time = random.nextLong();
			final long random1 = random.nextLong();
			final long random2 = random.nextLong();
			UlidStruct struct0 = new UlidStruct(time, random1, random2); // <-- under test

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
			UlidStruct struct0 = new UlidStruct(time, random1, random2);

			String string1 = toString(struct0);
			UlidStruct struct1 = new UlidStruct(string1); // <-- under test
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
			UlidStruct struct0 = new UlidStruct(uuid0); // <-- under test

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
			UlidStruct struct0 = new UlidStruct(time, random1, random2);

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
			UlidStruct struct0 = new UlidStruct(time, random1, random2);

			UUID uuid1 = toUuid(struct0);
			UUID uuid2 = struct0.toUuid(); // <-- under test
			assertEquals(uuid1, uuid2);
		}
	}

	public UlidStruct fromString(String string) {

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

		return new UlidStruct(time, random1, random2);
	}

	public UUID toUuid(UlidStruct struct) {

		long time = struct.time & 0xffffffffffffL;
		long random1 = struct.random1 & 0xffffffffffL;
		long random2 = struct.random2 & 0xffffffffffL;

		final long msb = (time << 16) | (random1 >>> 24);
		final long lsb = (random1 << 40) | random2;

		return new UUID(msb, lsb);
	}

	public String toString(UlidStruct struct) {

		final String tzero = "0000000000";
		final String rzero = "00000000";

		String time = Long.toUnsignedString(struct.time, 32);
		String random1 = Long.toUnsignedString(struct.random1, 32);
		String random2 = Long.toUnsignedString(struct.random2, 32);

		time = tzero.substring(0, tzero.length() - time.length()) + time;
		random1 = rzero.substring(0, rzero.length() - random1.length()) + random1;
		random2 = rzero.substring(0, rzero.length() - random2.length()) + random2;

		time = transliterate(time, ALPHABET_JAVA, ALPHABET_CROCKFORD);
		random1 = transliterate(random1, ALPHABET_JAVA, ALPHABET_CROCKFORD);
		random2 = transliterate(random2, ALPHABET_JAVA, ALPHABET_CROCKFORD);

		return time + random1 + random2;
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
