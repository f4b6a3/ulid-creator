package com.github.f4b6a3.ulid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public abstract class UlidFactoryTest {

	protected static final int DEFAULT_LOOP_MAX = 10_000;

	protected static final String DUPLICATE_UUID_MSG = "A duplicate ULID was created.";

	protected static final int THREAD_TOTAL = availableProcessors();

	protected static final Random RANDOM = new Random();

	protected static final long TIME_MASK = 0x0000ffffffffffffL;

	private static int availableProcessors() {
		int processors = Runtime.getRuntime().availableProcessors();
		if (processors < 4) {
			processors = 4;
		}
		return processors;
	}

	protected void checkNullOrInvalid(Ulid[] list) {
		for (Ulid ulid : list) {
			assertNotNull("ULID is null", ulid);
		}
	}

	protected void checkUniqueness(Ulid[] list) {

		HashSet<Ulid> set = new HashSet<>();

		for (Ulid ulid : list) {
			assertTrue(String.format("ULID is duplicated %s", ulid), set.add(ulid));
		}

		assertEquals("There are duplicated ULIDs", set.size(), list.length);
	}
	
	protected void checkCreationTime(Ulid[] list, long startTime, long endTime) {

		assertTrue("Start time was after end time", startTime <= endTime);

		for (Ulid ulid : list) {
			long creationTime = ulid.getTime();
			assertTrue("Creation time was before start time " + creationTime + " " + startTime,
					creationTime >= startTime);
			assertTrue("Creation time was after end time", creationTime <= endTime);
		}
	}

	protected static class TestThread extends Thread {

		public static Set<UUID> hashSet = new HashSet<>();
		private UlidFactory creator;
		private int loopLimit;

		public TestThread(UlidFactory creator, int loopLimit) {
			this.creator = creator;
			this.loopLimit = loopLimit;
		}

		public static void clearHashSet() {
			hashSet = new HashSet<>();
		}

		@Override
		public void run() {
			long timestamp = System.currentTimeMillis();
			for (int i = 0; i < loopLimit; i++) {
				synchronized (hashSet) {
					hashSet.add(creator.create(timestamp).toUuid());
				}
			}
		}
	}
}
