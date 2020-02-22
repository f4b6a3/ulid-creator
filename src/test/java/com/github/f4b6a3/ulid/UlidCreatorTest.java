package com.github.f4b6a3.ulid;

import org.junit.Test;

import com.github.f4b6a3.ulid.UlidCreator;
import com.github.f4b6a3.ulid.util.UlidUtil;

import static org.junit.Assert.*;
import java.util.Arrays;
import java.util.HashSet;

public class UlidCreatorTest {

	private static final int ULID_LENGTH = 26;
	private static final int DEFAULT_LOOP_MAX = 100_000;

	@Test
	public void testGetUlid() {
		String[] list = new String[DEFAULT_LOOP_MAX];

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

	private void checkNullOrInvalid(String[] list) {
		for (String ulid : list) {
			assertTrue("ULID is null", ulid != null);
			assertTrue("ULID is empty", !ulid.isEmpty());
			assertTrue("ULID length is wrong ", ulid.length() == ULID_LENGTH);
			assertTrue("ULID is not valid", UlidUtil.isValid(ulid, /* strict */ true));
		}
	}

	private void checkUniqueness(String[] list) {

		HashSet<String> set = new HashSet<>();

		for (String ulid : list) {
			assertTrue(String.format("ULID is duplicated %s", ulid), set.add(ulid));
		}

		assertTrue("There are duplicated ULIDs", set.size() == list.length);
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
			assertTrue("The ULID list is not ordered", list[i].equals(other[i]));
		}
	}

}
