package com.github.f4b6a3;

import java.util.HashSet;
import java.util.UUID;

import com.github.f4b6a3.ulid.UlidCreator;
import com.github.f4b6a3.ulid.creator.UlidBasedGuidCreator;
import com.github.f4b6a3.ulid.exception.UlidCreatorException;
import com.github.f4b6a3.ulid.timestamp.FixedTimestampStretegy;

/**
 * 
 * This test starts many threads that keep requesting thousands of ULIDs to a
 * single generator.
 * 
 * This is is not included in the {@link TestSuite} because it takes a long time
 * to finish.
 */
public class UniquenessTest {

	private int threadCount; // Number of threads to run
	private int requestCount; // Number of requests for thread

	// private long[][] cacheLong; // Store values generated per thread
	private HashSet<UUID> hashSet;

	private boolean verbose; // Show progress or not

	// GUID creator based on ULID spec
	private UlidBasedGuidCreator creator;

	/**
	 * Initialize the test.
	 * 
	 * @param threadCount
	 * @param requestCount
	 * @param creator
	 */
	public UniquenessTest(int threadCount, int requestCount, UlidBasedGuidCreator creator, boolean progress) {
		this.threadCount = threadCount;
		this.requestCount = requestCount;
		this.creator = creator;
		this.verbose = progress;
		this.initCache();
	}

	private void initCache() {
		this.hashSet = new HashSet<>();
	}

	/**
	 * Initialize and start the threads.
	 */
	public void start() {

		Thread[] threads = new Thread[this.threadCount];

		// Instantiate and start many threads
		for (int i = 0; i < this.threadCount; i++) {
			threads[i] = new Thread(new UniquenessTestThread(i, verbose));
			threads[i].start();
		}

		// Wait all the threads to finish
		for (Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	public class UniquenessTestThread implements Runnable {

		private int id;
		private boolean verbose;

		public UniquenessTestThread(int id, boolean verbose) {
			this.id = id;
			this.verbose = verbose;
		}

		/**
		 * Run the test.
		 */
		@Override
		public void run() {

			int progress = 0;
			int max = requestCount;

			for (int i = 0; i < max; i++) {

				// Request a UUID
				UUID uuid = null;
				try {
					uuid = creator.create();
				} catch (UlidCreatorException e) {
					// Ignore the overrun exception and try again
					uuid = creator.create();
				}

				if (verbose) {
					// Calculate and show progress
					progress = (int) ((i * 1.0 / max) * 100);
					if (progress % 10 == 0) {
						System.out.println(String.format("[Thread %06d] %s %s %s%%", id, uuid, i, (int) progress));
					}
				}
				synchronized (hashSet) {
					// Insert the value in cache, if it does not exist in it.
					if (!hashSet.add(uuid)) {
						System.err.println(
								String.format("[Thread %06d] %s %s %s%% [DUPLICATE]", id, uuid, i, (int) progress));
					}
				}
			}

			if (verbose) {
				// Finished
				System.out.println(String.format("[Thread %06d] Done.", id));
			}
		}
	}

	public static void execute(boolean verbose, int threadCount, int requestCount) {
		UlidBasedGuidCreator creator = UlidCreator.getUlidBasedCreator()
				.withTimestampStrategy(new FixedTimestampStretegy(System.currentTimeMillis()));

		UniquenessTest test = new UniquenessTest(threadCount, requestCount, creator, verbose);
		test.start();
	}

	public static void main(String[] args) {
		boolean verbose = true;
		int threadCount = 16; // Number of threads to run
		int requestCount = 1_000_000; // Number of requests for thread
		execute(verbose, threadCount, requestCount);
	}
}
