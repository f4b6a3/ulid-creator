package com.github.f4b6a3.ulid.ulid;

import org.junit.Test;

import com.github.f4b6a3.ulid.UlidCreator;
import com.github.f4b6a3.ulid.util.UlidUtil;

import static org.junit.Assert.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

public class UlidCreatorUuidTest {

	private static final int DEFAULT_LOOP_MAX = 100_000;

	@Test
	public void testGetUlid() {
		UUID[] list = new UUID[DEFAULT_LOOP_MAX];

		long startTime = System.currentTimeMillis();

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = UlidCreator.getUlid();
		}

		long endTime = System.currentTimeMillis();

		checkNullOrInvalid(list);
		checkUniqueness(list);
		checkOrdering(list);
		checkCreationTime(list, startTime, endTime);
	}

	@Test
	public void testGetUlid4() {
		UUID[] list = new UUID[DEFAULT_LOOP_MAX];

		long startTime = System.currentTimeMillis();

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = UlidCreator.getUlid4();
		}

		long endTime = System.currentTimeMillis();

		checkNullOrInvalid(list);
		checkUniqueness(list);
		checkOrdering(list);
		checkCreationTime(list, startTime, endTime);
		checkVersion4(list);
	}

	private void checkNullOrInvalid(UUID[] list) {
		for (UUID ulid : list) {
			assertNotNull("ULID is null", ulid);
		}
	}

	private void checkUniqueness(UUID[] list) {

		HashSet<UUID> set = new HashSet<>();

		for (UUID ulid : list) {
			assertTrue(String.format("ULID is duplicated %s", ulid), set.add(ulid));
		}

		assertEquals("There are duplicated ULIDs", set.size(), list.length);
	}

	private void checkCreationTime(UUID[] list, long startTime, long endTime) {

		assertTrue("Start time was after end time", startTime <= endTime);

		for (UUID ulid : list) {
			long creationTime = UlidUtil.extractUnixMilliseconds(ulid);
			assertTrue("Creation time was before start time " + creationTime + " " + startTime,
					creationTime >= startTime);
			assertTrue("Creation time was after end time", creationTime <= endTime);
		}
	}

	private void checkOrdering(UUID[] list) {
		UUID[] other = Arrays.copyOf(list, list.length);
		Arrays.sort(other);

		for (int i = 0; i < list.length; i++) {
			assertEquals("The ULID list is not ordered", list[i], other[i]);
		}
	}

	private void checkVersion4(UUID[] list) {
		for (UUID uuid : list) {
			assertEquals(String.format("ULID is is not RFC-4122 version 4 %s", uuid), 4, uuid.version());
			assertEquals(String.format("ULID is is not RFC-4122 variant 2 %s", uuid), 2, uuid.variant());
		}
	}
}
