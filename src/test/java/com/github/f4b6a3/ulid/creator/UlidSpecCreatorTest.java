package com.github.f4b6a3.ulid.creator;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.junit.Test;

import com.github.f4b6a3.ulid.UlidCreator;
import com.github.f4b6a3.ulid.strategy.timestamp.FixedTimestampStretegy;

import static org.junit.Assert.*;

public class UlidSpecCreatorTest {

	private static final int DEFAULT_LOOP_MAX = 1_000_000;

	private static final long TIMESTAMP = System.currentTimeMillis();

	private static final Random RANDOM = new Random();

	protected static final String DUPLICATE_UUID_MSG = "A duplicate ULID was created.";

	protected static final int THREAD_TOTAL = availableProcessors();

	private static int availableProcessors() {
		int processors = Runtime.getRuntime().availableProcessors();
		if (processors < 4) {
			processors = 4;
		}
		return processors;
	}

	@Test
	public void testRandomMostSignificantBits() {

		UlidSpecCreator creator = new UlidSpecCreator();
		creator.withTimestampStrategy(new FixedTimestampStretegy(TIMESTAMP));

		UUID uuid = creator.create();
		long firstRand1 = creator.extractRandom1(uuid);
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			uuid = creator.create();

		}

		long lastRand1 = creator.extractRandom1(uuid);
		long expected1 = firstRand1;
		assertEquals(String.format("The last high random should be iqual to the first %s.", expected1), expected1,
				lastRand1);

		creator.withTimestampStrategy(new FixedTimestampStretegy(TIMESTAMP + 1));
		uuid = creator.create();
		lastRand1 = uuid.getMostSignificantBits();
		assertNotEquals("The last high random should be random after timestamp changed.", firstRand1, lastRand1);
	}

	@Test
	public void testRandomLeastSignificantBits() {

		UlidSpecCreator creator = new UlidSpecCreator();
		creator.withTimestampStrategy(new FixedTimestampStretegy(TIMESTAMP));

		UUID uuid = creator.create();
		long firstRnd2 = creator.extractRandom2(uuid);
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			uuid = creator.create();
		}

		long lastRand2 = creator.extractRandom2(uuid);
		long expected = firstRnd2 + DEFAULT_LOOP_MAX;
		assertEquals(String.format("The last low random should be iqual to %s.", expected), expected, lastRand2);

		long notExpected = firstRnd2 + DEFAULT_LOOP_MAX + 1;
		creator.withTimestampStrategy(new FixedTimestampStretegy(TIMESTAMP + 1));
		uuid = creator.create();
		lastRand2 = uuid.getLeastSignificantBits();
		assertNotEquals("The last low random should be random after timestamp changed.", notExpected, lastRand2);
	}

	@Test
	public void testIncrementOfRandomLeastSignificantBits() {

		UlidSpecCreator creator = new UlidSpecCreator();
		creator.withTimestampStrategy(new FixedTimestampStretegy(TIMESTAMP));

		creator.create();
		long random2 = creator.random2;

		UUID uuid = new UUID(0, 0);
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			uuid = creator.create();
		}

		long expected2 = random2 + DEFAULT_LOOP_MAX;
		long rand2 = creator.random2;
		assertEquals("Wrong low random after loop.", expected2, rand2);

		rand2 = creator.extractRandom2(uuid);
		assertEquals("Wrong low random after loop.", expected2, rand2);
	}

	@Test
	public void testIncrementOfRandomMostSignificantBits() {

		UlidSpecCreator creator = new UlidSpecCreator();
		creator.withTimestampStrategy(new FixedTimestampStretegy(TIMESTAMP));

		creator.create();
		long random1 = creator.random1;

		UUID uuid = new UUID(0, 0);
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			uuid = creator.create();
		}

		long expected1 = random1;
		long rand1 = creator.random1;
		assertEquals("Wrong high random after loop.", expected1, rand1);

		rand1 = creator.extractRandom1(uuid);
		assertEquals("Wrong high random after loop.", expected1, rand1);
	}

	@Test
	public void testIncrementRandomComponentMaximum1() {

		long random1 = 0x000000ffffffffffL;
		long random2 = 0x000000ffffffffffL;

		long max1 = random1 | UlidSpecCreatorMock.INCREMENT_MAX;
		long max2 = random2 | UlidSpecCreatorMock.INCREMENT_MAX;

		random1 = max1;
		random2 = max2 - DEFAULT_LOOP_MAX;

		random2--; // Adjust

		UlidSpecCreatorMock creator = new UlidSpecCreatorMock(random1, random2, max1, max2, TIMESTAMP);
		creator.withTimestampStrategy(new FixedTimestampStretegy(TIMESTAMP));

		UUID uuid = new UUID(0, 0);
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			uuid = creator.create();
		}

		long hi1 = random1 & UlidSpecCreatorMock.HALF_RANDOM_COMPONENT;
		long lo1 = random2 & UlidSpecCreatorMock.HALF_RANDOM_COMPONENT;
		String concat1 = (Long.toHexString(hi1) + Long.toHexString(lo1));
		BigInteger bigint1 = new BigInteger(concat1, 16);
		long hi2 = creator.extractRandom1(uuid);
		long lo2 = creator.extractRandom2(uuid);
		String concat2 = (Long.toHexString(hi2) + Long.toHexString(lo2));
		BigInteger bigint2 = new BigInteger(concat2, 16);
		assertEquals(bigint1.add(BigInteger.valueOf(DEFAULT_LOOP_MAX)), bigint2);

		// This line resets the random component
		uuid = creator.create();
	}

	@Test
	public void testIncrementRandomComponentMaximum2() {

		long random1 = (RANDOM.nextLong() & UlidSpecCreatorMock.HALF_RANDOM_COMPONENT);
		long random2 = (RANDOM.nextLong() & UlidSpecCreatorMock.HALF_RANDOM_COMPONENT);

		long max1 = random1 | UlidSpecCreatorMock.INCREMENT_MAX;
		long max2 = random2 | UlidSpecCreatorMock.INCREMENT_MAX;

		random1 = max1;
		random2 = max2 - DEFAULT_LOOP_MAX;

		random2--; // Adjust

		UlidSpecCreatorMock creator = new UlidSpecCreatorMock(random1, random2, max1, max2, TIMESTAMP);
		creator.withTimestampStrategy(new FixedTimestampStretegy(TIMESTAMP));

		UUID uuid = new UUID(0, 0);
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			uuid = creator.create();
		}

		long rand1 = creator.extractRandom1(uuid);
		long expected1 = (max1 & UlidSpecCreatorMock.HALF_RANDOM_COMPONENT);
		assertEquals("Incorrect high random after loop.", expected1, rand1);

		long rand2 = creator.extractRandom2(uuid);
		long expected2 = (max2 & UlidSpecCreatorMock.HALF_RANDOM_COMPONENT) - 1;
		assertEquals("Incorrect low random after loop.", expected2, rand2);

		long hi1 = random1 & UlidSpecCreatorMock.HALF_RANDOM_COMPONENT;
		long lo1 = random2 & UlidSpecCreatorMock.HALF_RANDOM_COMPONENT;
		String concat1 = (Long.toHexString(hi1) + Long.toHexString(lo1));
		BigInteger bigint1 = new BigInteger(concat1, 16);
		long hi2 = creator.extractRandom1(uuid);
		long lo2 = creator.extractRandom2(uuid);
		String concat2 = (Long.toHexString(hi2) + Long.toHexString(lo2));
		BigInteger bigint2 = new BigInteger(concat2, 16);
		assertEquals(bigint1.add(BigInteger.valueOf(DEFAULT_LOOP_MAX)), bigint2);
		
		// This line resets the random component
		creator.create();
	}

	@Test
	public void testGetUlidParallelGeneratorsShouldCreateUniqueUlids() throws InterruptedException {

		Thread[] threads = new Thread[THREAD_TOTAL];
		TestThread.clearHashSet();

		// Instantiate and start many threads
		for (int i = 0; i < THREAD_TOTAL; i++) {
			threads[i] = new TestThread(UlidCreator.getUlidSpecCreator(), DEFAULT_LOOP_MAX);
			threads[i].start();
		}

		// Wait all the threads to finish
		for (Thread thread : threads) {
			thread.join();
		}

		// Check if the quantity of unique UUIDs is correct
		assertEquals(DUPLICATE_UUID_MSG, TestThread.hashSet.size(), (DEFAULT_LOOP_MAX * THREAD_TOTAL));
	}

	public static class TestThread extends Thread {

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
			for (int i = 0; i < loopLimit; i++) {
				synchronized (hashSet) {
					hashSet.add(creator.create());
				}
			}
		}
	}
}
