
package benchmark;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;

import com.github.f4b6a3.ulid.Ulid;
import com.github.f4b6a3.ulid.UlidCreator;

@Fork(1)
@Threads(1)
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
}
