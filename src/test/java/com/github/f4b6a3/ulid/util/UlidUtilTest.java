package com.github.f4b6a3.ulid.util;

import static org.junit.Assert.*;
import java.time.Instant;
import java.util.Random;

import org.junit.Test;

import com.github.f4b6a3.ulid.exception.InvalidUlidException;
import com.github.f4b6a3.ulid.util.internal.UlidStructTest;

import static com.github.f4b6a3.ulid.util.UlidUtil.*;

public class UlidUtilTest {

	// Date: 10889-08-02T05:31:50.655Z: 281474976710655 (2^48-1)
	private static final long TIMESTAMP_MAX = 0xffffffffffffL;
	private static final long HALF_RANDOM_MAX = 0xffffffffffL;

	private static final int DEFAULT_LOOP_MAX = 100_000;

	private static final Random RANDOM = new Random();

	@Test
	public void testExtractTimestamp1() {
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {

			long time = RANDOM.nextLong() & TIMESTAMP_MAX;
			long random1 = RANDOM.nextLong() & HALF_RANDOM_MAX;
			long random2 = RANDOM.nextLong() & HALF_RANDOM_MAX;

			String timeComponent = UlidStructTest.toTimeComponent(time);
			String randomComponent = UlidStructTest.toRandomComponent(random1, random2);

			String ulid = timeComponent + randomComponent;
			long result = extractTimestamp(ulid);
			assertEquals(time, result);
		}
	}

	@Test
	public void testExtractTimestamp2() {
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			
			String ulid;
			String timeComponent;
			String randomComponent;
			
			long time;
			long random1;
			long random2;
			
			time = RANDOM.nextLong() & TIMESTAMP_MAX;
			random1 = RANDOM.nextLong() & HALF_RANDOM_MAX;
			random2 = RANDOM.nextLong() & HALF_RANDOM_MAX;
			
			timeComponent = UlidStructTest.toTimeComponent(time);
			randomComponent = UlidStructTest.toRandomComponent(random1, random2);

			timeComponent = "7ZZZZZZZZZ";
			ulid = timeComponent + randomComponent;
			time = extractTimestamp(ulid);
			assertEquals(TIMESTAMP_MAX, time);
			
			timeComponent = "0000000000";
			ulid = timeComponent + randomComponent;
			time = extractTimestamp(ulid);
			assertEquals(0, time);

			try {
				// Test the first extra bit added by the base32 encoding
				char[] chars = timeComponent.toCharArray();
				chars[0] = 'G'; // GZZZZZZZZZ
				timeComponent = new String(chars);
				ulid = timeComponent + randomComponent;
				extractTimestamp(ulid);
				fail("Should throw an InvalidUlidException");
			} catch (InvalidUlidException e) {
				// success
			}

			try {
				// Test the second extra bit added by the base32 encoding
				char[] chars = timeComponent.toCharArray();
				chars[0] = '8'; // 8ZZZZZZZZZ
				timeComponent = new String(chars);
				ulid = timeComponent + randomComponent;
				extractTimestamp(ulid);
				fail("Should throw an InvalidUlidException");
			} catch (InvalidUlidException e) {
				// success
			}
		}
	}

	@Test
	public void testExtractUnixMilliseconds() {
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			long time = RANDOM.nextLong() & TIMESTAMP_MAX;
			long random1 = RANDOM.nextLong() & HALF_RANDOM_MAX;
			long random2 = RANDOM.nextLong() & HALF_RANDOM_MAX;
			String ulid = UlidStructTest.toString(time, random1, random2);
			long result = extractUnixMilliseconds(ulid);
			assertEquals(time, result);
		}
	}

	@Test
	public void testExtractInstant() {
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			long time = RANDOM.nextLong() & TIMESTAMP_MAX;
			Instant instant = Instant.ofEpochMilli(time);
			long random1 = RANDOM.nextLong() & HALF_RANDOM_MAX;
			long random2 = RANDOM.nextLong() & HALF_RANDOM_MAX;
			String ulid = UlidStructTest.toString(time, random1, random2);
			Instant result = extractInstant(ulid);
			assertEquals(instant, result);
		}
	}

	@Test
	public void testExtractTimestampComponent() {
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			long time = RANDOM.nextLong() & TIMESTAMP_MAX;
			long random1 = RANDOM.nextLong() & HALF_RANDOM_MAX;
			long random2 = RANDOM.nextLong() & HALF_RANDOM_MAX;
			String ulid = UlidStructTest.toString(time, random1, random2);
			
			char[] chars = ulid.toCharArray();
			char[] timeComponent = new char[10];
			System.arraycopy(chars, 0, timeComponent, 0, 10);
			String expected = new String(timeComponent); 

			String result = extractTimestampComponent(ulid);
			assertEquals(expected, result);
		}
	}
	
	@Test
	public void testExtractRandomnessComponent() {
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			long time = RANDOM.nextLong() & TIMESTAMP_MAX;
			long random1 = RANDOM.nextLong() & HALF_RANDOM_MAX;
			long random2 = RANDOM.nextLong() & HALF_RANDOM_MAX;
			String ulid = UlidStructTest.toString(time, random1, random2);
			
			char[] chars = ulid.toCharArray();
			char[] randomComponent = new char[16];
			System.arraycopy(chars, 10, randomComponent, 0, 16);
			String expected = new String(randomComponent); 

			String result = extractRandomnessComponent(ulid);
			assertEquals(expected, result);
		}
	}
}
