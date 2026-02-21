
package benchmark;

import com.github.f4b6a3.ulid.Ulid;
import com.github.f4b6a3.ulid.UlidCreator;
import org.openjdk.jmh.annotations.*;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Fork(1)
@Threads(4)
@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 3)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class Throughput {

	@Benchmark
	public UUID UUID_randomUUID() {
		return UUID.randomUUID();
	}

	@Benchmark
	public String UUID_randomUUID_toString() {
		return UUID.randomUUID().toString();
	}

	@Benchmark
	public Ulid Ulid_fast() {
		return Ulid.fast();
	}

	@Benchmark
	public String Ulid_fast_toString() {
		return Ulid.fast().toString();
	}

	@Benchmark
	public Ulid UlidCreator_getUlid() {
		return UlidCreator.getUlid();
	}

	@Benchmark
	public String UlidCreator_getUlid_toString() {
		return UlidCreator.getUlid().toString();
	}

	@Benchmark
	public Ulid UlidCreator_getMonotonicUlid() {
		return UlidCreator.getMonotonicUlid();
	}

	@Benchmark
	public String UlidCreator_getMonotonicUlid_toString() {
		return UlidCreator.getMonotonicUlid().toString();
	}

	@Benchmark
	public Ulid UlidCreator_getHashUlid() {
		return UlidCreator.getHashUlid(0L, "this is a test");
	}

	@Benchmark
	public String UlidCreator_getHashUlidString() {
		return UlidCreator.getHashUlid(0L, "this is a test").toString();
	}
}
