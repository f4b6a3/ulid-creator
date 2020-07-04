package com.github.f4b6a3.ulid.util;

import static org.junit.Assert.*;
import java.util.UUID;

import org.junit.Test;

import com.github.f4b6a3.ulid.UlidCreator;
import com.github.f4b6a3.ulid.util.UlidConverter;
import com.github.f4b6a3.ulid.util.UlidValidator;

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
}
