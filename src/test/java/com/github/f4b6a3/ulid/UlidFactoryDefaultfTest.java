package com.github.f4b6a3.ulid;

import org.junit.Test;

import com.github.f4b6a3.ulid.Ulid;
import com.github.f4b6a3.ulid.UlidCreator;
import com.github.f4b6a3.ulid.UlidFactory;

import static org.junit.Assert.*;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;
import java.util.SplittableRandom;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.IntFunction;
import java.util.function.LongSupplier;

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

	@Test
	public void testDefault() {
		UlidFactory factory = new UlidFactory();
		assertNotNull(factory.create());
	}

	@Test
	public void testWithRandom() {
		{
			Random random = new Random();
			UlidFactory factory = UlidFactory.newInstance(random);
			assertNotNull(factory.create());
		}
		{
			SecureRandom random = new SecureRandom();
			UlidFactory factory = UlidFactory.newInstance(random);
			assertNotNull(factory.create());
		}
	}

	@Test
	public void testWithRandomNull() {
		UlidFactory factory = UlidFactory.newInstance((Random) null);
		assertNotNull(factory.create());
	}

	@Test
	public void testWithRandomFunction() {
		{
			SplittableRandom random = new SplittableRandom();
			LongSupplier function = () -> random.nextLong();
			UlidFactory factory = UlidFactory.newInstance(function);
			assertNotNull(factory.create());
		}
		{
			IntFunction<byte[]> function = (length) -> {
				byte[] bytes = new byte[length];
				ThreadLocalRandom.current().nextBytes(bytes);
				return bytes;
			};
			UlidFactory factory = UlidFactory.newInstance(function);
			assertNotNull(factory.create());
		}
	}

	@Test
	public void testWithRandomFunctionNull() {
		{
			UlidFactory factory = UlidFactory.newInstance((LongSupplier) null);
			assertNotNull(factory.create());
		}
		{
			UlidFactory factory = UlidFactory.newInstance((IntFunction<byte[]>) null);
			assertNotNull(factory.create());
		}
	}

	@Test
	public void testByteRandomNextLong() {

		for (int i = 0; i < 10; i++) {
			byte[] bytes = new byte[Long.BYTES];
			(new Random()).nextBytes(bytes);
			long number = ByteBuffer.wrap(bytes).getLong();
			UlidFactory.IRandom random = new UlidFactory.ByteRandom((x) -> bytes);
			assertEquals(number, random.nextLong());
		}

		for (int i = 0; i < 10; i++) {

			int longs = 10;
			int size = Long.BYTES * longs;

			byte[] bytes = new byte[size];
			(new Random()).nextBytes(bytes);
			ByteBuffer buffer1 = ByteBuffer.wrap(bytes);
			ByteBuffer buffer2 = ByteBuffer.wrap(bytes);

			UlidFactory.IRandom random = new UlidFactory.ByteRandom((x) -> {
				byte[] octects = new byte[x];
				buffer1.get(octects);
				return octects;
			});

			for (int j = 0; j < longs; j++) {
				assertEquals(buffer2.getLong(), random.nextLong());
			}
		}
	}

	@Test
	public void testByteRandomNextBytes() {

		for (int i = 0; i < 10; i++) {
			byte[] bytes = new byte[Long.BYTES];
			(new Random()).nextBytes(bytes);
			UlidFactory.IRandom random = new UlidFactory.ByteRandom((x) -> bytes);
			assertEquals(Arrays.toString(bytes), Arrays.toString(random.nextBytes(Long.BYTES)));
		}

		for (int i = 0; i < 10; i++) {

			int ints = 10;
			int size = Long.BYTES * ints;

			byte[] bytes = new byte[size];
			(new Random()).nextBytes(bytes);
			ByteBuffer buffer1 = ByteBuffer.wrap(bytes);
			ByteBuffer buffer2 = ByteBuffer.wrap(bytes);

			UlidFactory.IRandom random = new UlidFactory.ByteRandom((x) -> {
				byte[] octects = new byte[x];
				buffer1.get(octects);
				return octects;
			});

			for (int j = 0; j < ints; j++) {
				byte[] octects = new byte[Long.BYTES];
				buffer2.get(octects);
				assertEquals(Arrays.toString(octects), Arrays.toString(random.nextBytes(Long.BYTES)));
			}
		}
	}

	@Test
	public void testLogRandomNextLong() {

		for (int i = 0; i < 10; i++) {
			byte[] bytes = new byte[Long.BYTES];
			(new Random()).nextBytes(bytes);
			long number = ByteBuffer.wrap(bytes).getLong();
			UlidFactory.IRandom random = new UlidFactory.LongRandom(() -> number);
			assertEquals(number, random.nextLong());
		}

		for (int i = 0; i < 10; i++) {

			int ints = 10;
			int size = Long.BYTES * ints;

			byte[] bytes = new byte[size];
			(new Random()).nextBytes(bytes);
			ByteBuffer buffer1 = ByteBuffer.wrap(bytes);
			ByteBuffer buffer2 = ByteBuffer.wrap(bytes);

			UlidFactory.IRandom random = new UlidFactory.LongRandom(() -> buffer1.getLong());

			for (int j = 0; j < ints; j++) {
				assertEquals(buffer2.getLong(), random.nextLong());
			}
		}
	}

	@Test
	public void testLogRandomNextBytes() {

		for (int i = 0; i < 10; i++) {
			byte[] bytes = new byte[Long.BYTES];
			(new Random()).nextBytes(bytes);
			long number = ByteBuffer.wrap(bytes).getLong();
			UlidFactory.IRandom random = new UlidFactory.LongRandom(() -> number);
			assertEquals(Arrays.toString(bytes), Arrays.toString(random.nextBytes(Long.BYTES)));
		}

		for (int i = 0; i < 10; i++) {

			int ints = 10;
			int size = Long.BYTES * ints;

			byte[] bytes = new byte[size];
			(new Random()).nextBytes(bytes);
			ByteBuffer buffer1 = ByteBuffer.wrap(bytes);
			ByteBuffer buffer2 = ByteBuffer.wrap(bytes);

			UlidFactory.IRandom random = new UlidFactory.LongRandom(() -> buffer1.getLong());

			for (int j = 0; j < ints; j++) {
				byte[] octects = new byte[Long.BYTES];
				buffer2.get(octects);
				assertEquals(Arrays.toString(octects), Arrays.toString(random.nextBytes(Long.BYTES)));
			}
		}
	}
}
