package com.github.f4b6a3.ulid;

import org.junit.Test;

import com.github.f4b6a3.ulid.Ulid;
import com.github.f4b6a3.ulid.UlidCreator;
import com.github.f4b6a3.ulid.UlidFactory;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Random;

public class UlidFactoryMonotonicTest extends UlidFactoryTest {

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

	private void checkOrdering(Ulid[] list) {
		Ulid[] other = Arrays.copyOf(list, list.length);
		Arrays.sort(other);

		for (int i = 0; i < list.length; i++) {
			assertEquals("The ULID list is not ordered", list[i], other[i]);
		}
	}

	@Test
	public void testGetMonotonicUlidInParallel() throws InterruptedException {

		Thread[] threads = new Thread[THREAD_TOTAL];
		TestThread.clearHashSet();

		// Instantiate and start many threads
		for (int i = 0; i < THREAD_TOTAL; i++) {
			UlidFactory factory = UlidFactory.newMonotonicInstance(new Random());
			threads[i] = new TestThread(factory, DEFAULT_LOOP_MAX);
			threads[i].start();
		}

		// Wait all the threads to finish
		for (Thread thread : threads) {
			thread.join();
		}

		// Check if the quantity of unique UUIDs is correct
		assertEquals(DUPLICATE_UUID_MSG, TestThread.hashSet.size(), (DEFAULT_LOOP_MAX * THREAD_TOTAL));
	}

	@Test
	public void testGetMonotonicUlidTime() {
		for (int i = 0; i < 100; i++) {
			long time = RANDOM.nextLong() & TIME_MASK;
			Ulid ulid = UlidCreator.getMonotonicUlid(time);
			assertEquals(time, ulid.getTime());
		}
	}
}
