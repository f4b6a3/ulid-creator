package com.github.f4b6a3.ulid;

import org.junit.Test;

import static org.junit.Assert.*;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Random;
import java.util.SplittableRandom;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.IntFunction;
import java.util.function.LongSupplier;

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

	@Test
	public void testGetMonotonicUlidAfterClockDrift() {

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

		IntFunction<byte[]> randomSupplier = UlidFactory.ByteRandom.newRandomFunction(new Random());
		UlidFactory factory = UlidFactory.newMonotonicInstance(randomSupplier, clock);

		long ms1 = factory.create().getTime(); // time
		long ms2 = factory.create().getTime(); // time + 0
		long ms3 = factory.create().getTime(); // time + 1
		long ms4 = factory.create().getTime(); // time + 2
		long ms5 = factory.create().getTime(); // time + 3 - 10000 (CLOCK DRIFT)
		long ms6 = factory.create().getTime(); // time + 4 - 10000 (CLOCK DRIFT)
		long ms7 = factory.create().getTime(); // time + 5
		assertEquals(ms1 + 0, ms2); // clock repeats.
		assertEquals(ms1 + 1, ms3); // clock advanced.
		assertEquals(ms1 + 2, ms4); // clock advanced.
		assertEquals(ms1 + 2, ms5); // CLOCK DRIFT! DON'T MOVE BACKWARDS!
		assertEquals(ms1 + 2, ms6); // CLOCK DRIFT! DON'T MOVE BACKWARDS!
		assertEquals(ms1 + 5, ms7); // clock advanced.
	}

	@Test
	public void testGetMonotonicUlidAfterLeapSecond() {

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

		IntFunction<byte[]> randomSupplier = UlidFactory.ByteRandom.newRandomFunction(new Random());
		UlidFactory factory = UlidFactory.newMonotonicInstance(randomSupplier, clock);

		long ms1 = factory.create().getTime(); // second
		long ms2 = factory.create().getTime(); // leap second

		assertEquals(ms1, ms2); // LEAP SECOND! DON'T MOVE BACKWARDS!
	}

	@Test
	public void testGetMonotonicUlidAfterRandomBitsOverflowFollowedByTimeBitsIncrement() {

		long time = Instant.parse("2021-12-31T23:59:59.999Z").toEpochMilli();
		long times[] = { time, time, time + 1, time + 2 };

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

		LongSupplier randomSupplier = () -> 0xffffffffffffffffL;
		UlidFactory factory = UlidFactory.newMonotonicInstance(randomSupplier, clock);

		Ulid ulid1 = factory.create();
		Ulid ulid2 = factory.create(); // time bits should be incremented here
		Ulid ulid3 = factory.create();
		Ulid ulid4 = factory.create();

		assertEquals(ulid1.getTime(), time);
		assertEquals(ulid2.getTime(), time + 1); // check if time bits increment occurred
		assertEquals(ulid3.getTime(), time + 1);
		assertEquals(ulid4.getTime(), time + 2);

		assertEquals(ulid1.getMostSignificantBits() & 0xffffL, 0xffffL);
		assertEquals(ulid2.getMostSignificantBits() & 0xffffL, 0x0000L);
		assertEquals(ulid3.getMostSignificantBits() & 0xffffL, 0x0000L);
		assertEquals(ulid4.getMostSignificantBits() & 0xffffL, 0xffffL);

		assertEquals(ulid1.getLeastSignificantBits(), 0xffffffffffffffffL);
		assertEquals(ulid2.getLeastSignificantBits(), 0x0000000000000000L);
		assertEquals(ulid3.getLeastSignificantBits(), 0x0000000000000001L);
		assertEquals(ulid4.getLeastSignificantBits(), 0xffffffffffffffffL);
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

	@Test
	public void testWithRandom() {
		Random random = new Random();
		UlidFactory factory = UlidFactory.newMonotonicInstance(random);
		assertNotNull(factory.create());
	}

	@Test
	public void testWithRandomNull() {
		UlidFactory factory = UlidFactory.newMonotonicInstance((Random) null);
		assertNotNull(factory.create());
	}

	@Test
	public void testWithRandomFunction() {
		{
			SplittableRandom random = new SplittableRandom();
			LongSupplier function = () -> random.nextLong();
			UlidFactory factory = UlidFactory.newMonotonicInstance(function);
			assertNotNull(factory.create());
		}
		{
			SplittableRandom random = new SplittableRandom();
			LongSupplier function = () -> random.nextLong();
			UlidFactory factory = UlidFactory.newMonotonicInstance(function, Clock.systemDefaultZone());
			assertNotNull(factory.create());
		}
		{
			IntFunction<byte[]> function = (length) -> {
				byte[] bytes = new byte[length];
				ThreadLocalRandom.current().nextBytes(bytes);
				return bytes;
			};
			UlidFactory factory = UlidFactory.newMonotonicInstance(function);
			assertNotNull(factory.create());
		}
		{
			IntFunction<byte[]> function = (length) -> {
				byte[] bytes = new byte[length];
				ThreadLocalRandom.current().nextBytes(bytes);
				return bytes;
			};
			UlidFactory factory = UlidFactory.newMonotonicInstance(function, Clock.systemDefaultZone());
			assertNotNull(factory.create());
		}
	}

	@Test
	public void testWithRandomFunctionNull() {
		{
			UlidFactory factory = UlidFactory.newMonotonicInstance((LongSupplier) null);
			assertNotNull(factory.create());
		}
		{
			UlidFactory factory = UlidFactory.newMonotonicInstance((IntFunction<byte[]>) null);
			assertNotNull(factory.create());
		}
	}
}
