package com.github.f4b6a3.ulid.ulid;

import org.junit.BeforeClass;
import org.junit.Test;

import com.github.f4b6a3.ulid.UlidCreator;
import com.github.f4b6a3.ulid.creator.UlidBasedGuidCreator;
import com.github.f4b6a3.ulid.util.UlidUtil;
import com.github.f4b6a3.ulid.util.UlidValidator;

import static org.junit.Assert.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class UlidCreatorTest {

	private static int processors;

	private static final int ULID_LENGTH = 26;
	private static final int DEFAULT_LOOP_MAX = 100_000;

	private static final String DUPLICATE_UUID_MSG = "A duplicate ULID was created";

	@BeforeClass
	public static void beforeClass() {

		processors = Runtime.getRuntime().availableProcessors();
		if (processors < 4) {
			processors = 4;
		}
	}

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

	private void checkNullOrInvalid(String[] list) {
		for (String ulid : list) {
			assertTrue("ULID is null", ulid != null);
			assertTrue("ULID is empty", !ulid.isEmpty());
			assertTrue("ULID length is wrong ", ulid.length() == ULID_LENGTH);
			assertTrue("ULID is not valid", UlidValidator.isValid(ulid));
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

	@Test
	public void testGetUlidBasedGuidParallelGeneratorsShouldCreateUniqueUuids() throws InterruptedException {

		Thread[] threads = new Thread[processors];
		TestThread.clearHashSet();

		// Instantiate and start many threads
		for (int i = 0; i < processors; i++) {
			threads[i] = new TestThread(UlidCreator.getUlidBasedCreator(), DEFAULT_LOOP_MAX);
			threads[i].start();
		}

		// Wait all the threads to finish
		for (Thread thread : threads) {
			thread.join();
		}

		// Check if the quantity of unique UUIDs is correct
		assertTrue(DUPLICATE_UUID_MSG, TestThread.hashSet.size() == (DEFAULT_LOOP_MAX * processors));
	}

	private static class TestThread extends Thread {

		private static Set<UUID> hashSet = new HashSet<>();
		private UlidBasedGuidCreator creator;
		private int loopLimit;

		public TestThread(UlidBasedGuidCreator creator, int loopLimit) {
			this.creator = creator;
			this.loopLimit = loopLimit;
		}

		public static void clearHashSet() {
			hashSet = new HashSet<>();
		}

		@Override
		public void run() {
			for (int i = 0; i < loopLimit; i++) {
				synchronized (hashSet) {
					hashSet.add(creator.create());
				}
			}
		}
	}
}
