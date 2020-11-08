package com.github.f4b6a3.ulid.util;

import static org.junit.Assert.*;

import java.util.Random;
import java.util.UUID;

import org.junit.Test;

import com.github.f4b6a3.ulid.UlidCreator;
import com.github.f4b6a3.ulid.util.UlidConverter;
import com.github.f4b6a3.ulid.util.UlidValidator;
import com.github.f4b6a3.ulid.util.internal.UlidStruct;

public class UlidConverterTest {

	private static final int ULID_LENGTH = 26;
	private static final int DEFAULT_LOOP_MAX = 100_000;

	@Test
	public void testToAndFromUlid() {

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {

			UUID uuid1 = UlidCreator.getUlid();
			String ulid = UlidConverter.toString(uuid1);

			assertNotNull("ULID is null", ulid);
			assertTrue("ULID is empty", !ulid.isEmpty());
			assertEquals("ULID length is wrong ", ULID_LENGTH, ulid.length());
			assertTrue("ULID is not valid", UlidValidator.isValid(ulid));

			UUID uuid2 = UlidConverter.fromString(ulid);
			assertEquals("Result ULID is different from original ULID", uuid1, uuid2);

		}
	}

	@Test
	public void testToString1() {

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {

			Random random = new Random();
			final long time = random.nextLong();
			final long random1 = random.nextLong();
			final long random2 = random.nextLong();
			UlidStruct struct0 = UlidStruct.of(time, random1, random2);

			String string1 = struct0.toString();
			UlidStruct struct1 = UlidStruct.of(string1);

			assertEquals(struct0.time, struct1.time);
			assertEquals(struct0.random1, struct1.random1);
			assertEquals(struct0.random2, struct1.random2);
		}
	}

	@Test
	public void testToString2() {

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {

			UUID ulid0 = UlidCreator.getUlid();
			UlidStruct struct0 = UlidStruct.of(ulid0);

			String string1 = UlidConverter.toString(ulid0);
			UlidStruct struct1 = UlidStruct.of(string1);

			assertEquals(struct0.time, struct1.time);
			assertEquals(struct0.random1, struct1.random1);
			assertEquals(struct0.random2, struct1.random2);
		}
	}

	@Test
	public void testToString3() {

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {

			UUID ulid0 = UlidCreator.getUlid();
			UlidStruct struct0 = UlidStruct.of(ulid0);

			String string1 = struct0.toString();
			UlidStruct struct1 = UlidStruct.of(string1);

			assertEquals(struct0.time, struct1.time);
			assertEquals(struct0.random1, struct1.random1);
			assertEquals(struct0.random2, struct1.random2);
		}
	}

	@Test
	public void testToString4() {

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {

			UUID ulid0 = UlidCreator.getUlid();
			UlidStruct struct0 = UlidStruct.of(ulid0);

			String string1 = UlidConverter.toString(ulid0);
			UlidStruct struct1 = UlidStruct.of(string1);

			String string2 = struct0.toString();
			UlidStruct struct2 = UlidStruct.of(string2);

			assertEquals(string1, string2);
			
			assertEquals(struct0.time, struct1.time);
			assertEquals(struct0.random1, struct1.random1);
			assertEquals(struct0.random2, struct1.random2);

			assertEquals(struct0.time, struct2.time);
			assertEquals(struct0.random1, struct2.random1);
			assertEquals(struct0.random2, struct2.random2);
		}
	}
}
