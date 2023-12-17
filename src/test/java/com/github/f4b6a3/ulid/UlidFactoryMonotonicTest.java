package com.github.f4b6a3.ulid;

import org.junit.Test;

import static org.junit.Assert.*;

import java.time.Clock;
import java.time.Instant;
import java.util.Arrays;
import java.util.Random;
import java.util.SplittableRandom;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
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
		long times[] = { time + 0, time + 1, time + 2, time + 3, time + 4 - diff, time + 5 - diff, time + 6 - diff };

		AtomicInteger i = new AtomicInteger();
		LongSupplier timeFunction = () -> times[i.getAndIncrement() % times.length];

		LongSupplier randomFunction = () -> 0;
		UlidFactory factory = UlidFactory.newMonotonicInstance(randomFunction, timeFunction);

		Ulid ulid1 = factory.create();
		Ulid ulid2 = factory.create();
		Ulid ulid3 = factory.create();
		Ulid ulid4 = factory.create();
		Ulid ulid5 = factory.create();
		Ulid ulid6 = factory.create();
		Ulid ulid7 = factory.create();

		long t1 = ulid1.getTime(); // time + 0
		long t2 = ulid2.getTime(); // time + 1
		long t3 = ulid3.getTime(); // time + 2
		long t4 = ulid4.getTime(); // time + 3
		long t5 = ulid5.getTime(); // time + 4 - 10000 (CLOCK DRIFT)
		long t6 = ulid6.getTime(); // time + 5 - 10000
		long t7 = ulid7.getTime(); // time + 6 - 10000

		long r1 = ulid1.getLeastSignificantBits(); // time + 0
		long r2 = ulid2.getLeastSignificantBits(); // time + 1
		long r3 = ulid3.getLeastSignificantBits(); // time + 2
		long r4 = ulid4.getLeastSignificantBits(); // time + 3
		long r5 = ulid5.getLeastSignificantBits(); // time + 4 - 10000 (CLOCK REGRESSION)
		long r6 = ulid6.getLeastSignificantBits(); // time + 5 - 10000
		long r7 = ulid7.getLeastSignificantBits(); // time + 6 - 10000

		assertEquals(time + 0, t1); // time + 0
		assertEquals(time + 1, t2); // time + 1
		assertEquals(time + 2, t3); // time + 2
		assertEquals(time + 3, t4); // time + 3
		assertEquals(time + 3, t5); // time + 4 - 10000 (CLOCK REGRESSION)
		assertEquals(time + 3, t6); // time + 5 - 10000
		assertEquals(time + 3, t7); // time + 5 - 10000

		assertEquals(0, r1); // time + 0
		assertEquals(0, r2); // time + 1
		assertEquals(0, r3); // time + 2
		assertEquals(0, r4); // time + 3
		assertEquals(1, r5); // time + 4 - 10000 (CLOCK REGRESSION)
		assertEquals(2, r6); // time + 5 - 10000
		assertEquals(3, r7); // time + 5 - 10000
	}

	@Test
	public void testGetMonotonicUlidAfterLeapSecond() {

		// The best article about leap seconds:
		// (Unfortunately it can't be translated)
		// https://ntp.br/conteudo/artigo-leap-second/
		long time = Instant.parse("2021-12-31T23:59:59.000Z").toEpochMilli();
		long leap = time - 1000; // moving the clock hands 1 second backwards
		long times[] = { time, leap };

		AtomicInteger i = new AtomicInteger();
		LongSupplier timeFunction = () -> times[i.getAndIncrement() % times.length];

		LongSupplier randomFunction = () -> 0;
		UlidFactory factory = UlidFactory.newMonotonicInstance(randomFunction, timeFunction);

		// the clock moved normally
		Ulid ulid1 = factory.create();
		long t1 = ulid1.getTime();
		long r1 = ulid1.getLeastSignificantBits();
		assertEquals(time, t1);
		assertEquals(0, r1);

		// the clock moved backwards
		Ulid ulid2 = factory.create();
		long t2 = ulid2.getTime();
		long r2 = ulid2.getLeastSignificantBits();
		assertEquals(time, t2); // should freeze
		assertEquals(1, r2); // should increment
	}

	@Test
	public void testGetMonotonicUlidAfterRandomBitsOverflowFollowedByTimeBitsIncrement() {

		long time = Instant.parse("2021-12-31T23:59:59.999Z").toEpochMilli();
		long times[] = { time + 1, time + 2, time + 3, time, time, time };

		AtomicInteger i = new AtomicInteger();
		LongSupplier timeFunction = () -> times[i.getAndIncrement() % times.length];

		LongSupplier randomSupplier = () -> 0xffffffffffffffffL;
		UlidFactory factory = UlidFactory.newMonotonicInstance(randomSupplier, timeFunction);

		Ulid ulid1 = factory.create();
		Ulid ulid2 = factory.create();
		Ulid ulid3 = factory.create();
		Ulid ulid4 = factory.create();
		Ulid ulid5 = factory.create();
		Ulid ulid6 = factory.create();

		assertEquals(time + 1, ulid1.getTime());
		assertEquals(time + 2, ulid2.getTime());
		assertEquals(time + 3, ulid3.getTime());
		assertEquals(time + 4, ulid4.getTime());
		assertEquals(time + 4, ulid5.getTime());
		assertEquals(time + 4, ulid6.getTime());

		assertEquals(0xffffL, ulid1.getMostSignificantBits() & 0xffffL);
		assertEquals(0xffffL, ulid2.getMostSignificantBits() & 0xffffL);
		assertEquals(0xffffL, ulid3.getMostSignificantBits() & 0xffffL);
		assertEquals(0x0000L, ulid4.getMostSignificantBits() & 0xffffL);
		assertEquals(0x0000L, ulid5.getMostSignificantBits() & 0xffffL);
		assertEquals(0x0000L, ulid6.getMostSignificantBits() & 0xffffL);

		assertEquals(0xffffffffffffffffL, ulid1.getLeastSignificantBits());
		assertEquals(0xffffffffffffffffL, ulid2.getLeastSignificantBits());
		assertEquals(0xffffffffffffffffL, ulid3.getLeastSignificantBits());
		assertEquals(0x0000000000000000L, ulid4.getLeastSignificantBits());
		assertEquals(0x0000000000000001L, ulid5.getLeastSignificantBits());
		assertEquals(0x0000000000000002L, ulid6.getLeastSignificantBits());
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
			UlidFactory factory = UlidFactory.newMonotonicInstance(function, () -> Clock.systemUTC().millis());
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
			UlidFactory factory = UlidFactory.newMonotonicInstance(function, () -> Clock.systemUTC().millis());
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
