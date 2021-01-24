package com.github.f4b6a3.ulid.ulid;

import org.junit.Test;

import com.github.f4b6a3.ulid.Ulid;
import com.github.f4b6a3.ulid.UlidCreator;

import static org.junit.Assert.*;
import java.util.Arrays;
import java.util.HashSet;

public class UlidCreatorMonotonicTest {

	private static final int DEFAULT_LOOP_MAX = 100_000;

	@Test
	public void testGetUlid() {
		Ulid[] list = new Ulid[DEFAULT_LOOP_MAX];

		long startTime = System.currentTimeMillis();

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = UlidCreator.getMonotonicUlid();
		}

		long endTime = System.currentTimeMillis();

		checkNullOrInvalid(list);
		checkUniqueness(list);
		checkOrdering(list);
		checkCreationTime(list, startTime, endTime);
	}

	private void checkNullOrInvalid(Ulid[] list) {
		for (Ulid ulid : list) {
			assertNotNull("ULID is null", ulid);
		}
	}

	private void checkUniqueness(Ulid[] list) {

		HashSet<Ulid> set = new HashSet<>();

		for (Ulid ulid : list) {
			assertTrue(String.format("ULID is duplicated %s", ulid), set.add(ulid));
		}

		assertEquals("There are duplicated ULIDs", set.size(), list.length);
	}

	private void checkCreationTime(Ulid[] list, long startTime, long endTime) {

		assertTrue("Start time was after end time", startTime <= endTime);

		for (Ulid ulid : list) {
			long creationTime = ulid.getTime();
			assertTrue("Creation time was before start time " + creationTime + " " + startTime,
					creationTime >= startTime);
			assertTrue("Creation time was after end time", creationTime <= endTime);
		}
	}

	private void checkOrdering(Ulid[] list) {
		Ulid[] other = Arrays.copyOf(list, list.length);
		Arrays.sort(other);

		for (int i = 0; i < list.length; i++) {
			assertEquals("The ULID list is not ordered", list[i], other[i]);
		}
	}
}
