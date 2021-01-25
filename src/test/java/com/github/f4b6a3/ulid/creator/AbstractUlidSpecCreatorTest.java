package com.github.f4b6a3.ulid.creator;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public abstract class AbstractUlidSpecCreatorTest {

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

	protected static class TestThread extends Thread {

		public static Set<UUID> hashSet = new HashSet<>();
		private UlidSpecCreator creator;
		private int loopLimit;

		public TestThread(UlidSpecCreator creator, int loopLimit) {
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
