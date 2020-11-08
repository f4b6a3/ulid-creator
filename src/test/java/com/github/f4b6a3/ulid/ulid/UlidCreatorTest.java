package com.github.f4b6a3.ulid.ulid;

import org.junit.Test;

import com.github.f4b6a3.ulid.UlidCreator;
import com.github.f4b6a3.ulid.util.UlidConverter;
import com.github.f4b6a3.ulid.util.UlidUtil;
import com.github.f4b6a3.ulid.util.UlidValidator;

import static org.junit.Assert.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

public class UlidCreatorTest {

	private static final int ULID_LENGTH = 26;
	private static final int DEFAULT_LOOP_MAX = 100_000;

	@Test
	public void testGetUlid() {
		String[] list = new String[DEFAULT_LOOP_MAX];

		long startTime = System.currentTimeMillis();

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = UlidCreator.getUlidString();
		}

		long endTime = System.currentTimeMillis();

		checkNullOrInvalid(list);
		checkUniqueness(list);
		checkOrdering(list);
		checkCreationTime(list, startTime, endTime);
	}
	
	@Test
	public void testGetUlid4() {
		String[] list = new String[DEFAULT_LOOP_MAX];

		long startTime = System.currentTimeMillis();

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = UlidCreator.getUlidString4();
		}

		long endTime = System.currentTimeMillis();

		checkNullOrInvalid(list);
		checkUniqueness(list);
		checkOrdering(list);
		checkCreationTime(list, startTime, endTime);
		checkVersion4(list);
	}

	private void checkNullOrInvalid(String[] list) {
		for (String ulid : list) {
			assertNotNull("ULID is null", ulid);
			assertTrue("ULID is empty", !ulid.isEmpty());
			assertEquals("ULID length is wrong", ULID_LENGTH, ulid.length());
			assertTrue("ULID is not valid", UlidValidator.isValid(ulid));
		}
	}

	private void checkUniqueness(String[] list) {

		HashSet<String> set = new HashSet<>();

		for (String ulid : list) {
			assertTrue(String.format("ULID is duplicated %s", ulid), set.add(ulid));
		}

		assertEquals("There are duplicated ULIDs", set.size(), list.length);
	}

	private void checkCreationTime(String[] list, long startTime, long endTime) {

		assertTrue("Start time was after end time", startTime <= endTime);

		for (String ulid : list) {
			long creationTime = UlidUtil.extractTimestamp(ulid);
			assertTrue("Creation time was before start time " + creationTime + " " + startTime,
					creationTime >= startTime);
			assertTrue("Creation time was after end time", creationTime <= endTime);
		}
	}

	private void checkOrdering(String[] list) {
		String[] other = Arrays.copyOf(list, list.length);
		Arrays.sort(other);

		for (int i = 0; i < list.length; i++) {
			assertEquals("The ULID list is not ordered", list[i], other[i]);
		}
	}
	
	private void checkVersion4(String[] list) {
		for (String ulid : list) {
			UUID uuid = UlidConverter.fromString(ulid);
			assertEquals(String.format("ULID is is not version 4 %s", uuid), 4, uuid.version());
		}
	}
}
