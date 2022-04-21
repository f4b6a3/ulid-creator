package com.github.f4b6a3.ulid;

import org.junit.Test;

import com.github.f4b6a3.ulid.Ulid;
import com.github.f4b6a3.ulid.UlidCreator;
import com.github.f4b6a3.ulid.UlidFactory;

import static org.junit.Assert.*;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Random;
import java.util.function.Supplier;

public class UlidFactoryDefaultfTest extends UlidFactoryTest {

	@Test
	public void testGetUlid() {
		Ulid[] list = new Ulid[DEFAULT_LOOP_MAX];

		long startTime = System.currentTimeMillis();

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = UlidCreator.getUlid();
		}

		long endTime = System.currentTimeMillis();

		checkNullOrInvalid(list);
		checkUniqueness(list);
		checkCreationTime(list, startTime, endTime);
	}

	@Test
	public void testClockDrift() {

		long diff = UlidFactory.MonotonicFunction.CLOCK_DRIFT_TOLERANCE;
		long time = Instant.parse("2021-12-31T23:59:59.000Z").toEpochMilli();
		long times[] = { time, time + 0, time + 1, time + 2, time + 3 - diff, time + 4 - diff, time + 5 };

		Clock clock = new Clock() {
			private int i;

			@Override
			public long millis() {
				return times[i++ % times.length];
			}

			@Override
			public ZoneId getZone() {
				return null;
			}

			@Override
			public Clock withZone(ZoneId zone) {
				return null;
			}

			@Override
			public Instant instant() {
				return null;
			}
		};

		Supplier<byte[]> randomSupplier = UlidFactory.getRandomSupplier(new Random());
		UlidFactory factory = UlidFactory.newInstance(randomSupplier, clock);

		long ms1 = factory.create().getTime(); // time
		long ms2 = factory.create().getTime(); // time + 0
		long ms3 = factory.create().getTime(); // time + 1
		long ms4 = factory.create().getTime(); // time + 2
		long ms5 = factory.create().getTime(); // time + 3 - 10000 (CLOCK DRIFT)
		long ms6 = factory.create().getTime(); // time + 4 - 10000 (CLOCK DRIFT)
		long ms7 = factory.create().getTime(); // time + 5
		assertEquals(times[0], ms1);
		assertEquals(times[1], ms2);
		assertEquals(times[2], ms3);
		assertEquals(times[3], ms4);
		assertEquals(times[4], ms5);
		assertEquals(times[5], ms6);
		assertEquals(times[6], ms7);
	}

	@Test
	public void testLeapSecond() {

		long second = Instant.parse("2021-12-31T23:59:59.000Z").getEpochSecond();
		long leapSecond = second - 1; // simulate a leap second
		long times[] = { second, leapSecond };

		Clock clock = new Clock() {
			private int i;

			@Override
			public long millis() {
				return times[i++ % times.length] * 1000;
			}

			@Override
			public ZoneId getZone() {
				return null;
			}

			@Override
			public Clock withZone(ZoneId zone) {
				return null;
			}

			@Override
			public Instant instant() {
				return null;
			}
		};

		Supplier<byte[]> randomSupplier = UlidFactory.getRandomSupplier(new Random());
		UlidFactory factory = UlidFactory.newInstance(randomSupplier, clock);

		long ms1 = factory.create().getTime(); // second
		long ms2 = factory.create().getTime(); // leap second

		assertEquals(times[0] * 1000, ms1);
		assertEquals(times[1] * 1000, ms2);
	}

	@Test
	public void testGetUlidInParallel() throws InterruptedException {

		Thread[] threads = new Thread[THREAD_TOTAL];
		TestThread.clearHashSet();

		// Instantiate and start many threads
		for (int i = 0; i < THREAD_TOTAL; i++) {
			UlidFactory factory = UlidFactory.newInstance(new Random());
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
	public void testGetUlidTime() {
		for (int i = 0; i < 100; i++) {
			long time = RANDOM.nextLong() & TIME_MASK;
			Ulid ulid = UlidCreator.getUlid(time);
			assertEquals(time, ulid.getTime());
		}
	}
}
