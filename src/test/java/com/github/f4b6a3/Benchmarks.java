package com.github.f4b6a3;

// Add theese dependencies to pom.xml:
//
//		<dependency>
//			<groupId>org.openjdk.jmh</groupId>
//			<artifactId>jmh-core</artifactId>
//			<version>1.23</version>
//		</dependency>
//		<dependency>
//			<groupId>org.openjdk.jmh</groupId>
//			<artifactId>jmh-generator-annprocess</artifactId>
//			<version>1.23</version>
//		</dependency>

/*

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import com.github.f4b6a3.ulid.UlidCreator;

@Threads(1)
@State(Scope.Thread)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
public class Benchmarks {

	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	@OutputTimeUnit(TimeUnit.MILLISECONDS)
	public String getUlidThroughput() {
		return UlidCreator.getUlid();
	}

	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	@OutputTimeUnit(TimeUnit.NANOSECONDS)
	public String getUlidAverage() {
		return UlidCreator.getUlid();
	}
	
	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	@OutputTimeUnit(TimeUnit.MILLISECONDS)
	public UUID getGuidThroughput() {
		return UlidCreator.getGuid();
	}

	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	@OutputTimeUnit(TimeUnit.NANOSECONDS)
	public UUID getGuidAverage() {
		return UlidCreator.getGuid();
	}

	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	@OutputTimeUnit(TimeUnit.MILLISECONDS)
	public UUID getRandomUUIDThroughput() {
		return UUID.randomUUID();
	}

	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	@OutputTimeUnit(TimeUnit.NANOSECONDS)
	public UUID getRandomUUIDAverage() {
		return UUID.randomUUID();
	}

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder().include(MyBenchmark.class.getSimpleName()).forks(1).build();
		new Runner(opt).run();
	}
}
*/
