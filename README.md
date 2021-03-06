

# ULID Creator

A Java library for generating [ULIDs](https://github.com/ulid/spec).

* Generated in lexicographical order;
* Can be stored as a UUID/GUID;
* Can be stored as a string of 26 chars;
* Can be stored as an array of 16 bytes;
* String format is encoded to [Crockford's base32](https://www.crockford.com/base32.html);
* String format is URL safe and case insensitive.

How to Use
------------------------------------------------------

Create a ULID:

```java
Ulid ulid = UlidCreator.getUlid();
```

Create a Monotonic ULID:

```java
Ulid ulid = UlidCreator.getMonotonicUlid();
```

### Maven dependency

Add these lines to your `pom.xml`.

```xml
<!-- https://search.maven.org/artifact/com.github.f4b6a3/ulid-creator -->
<dependency>
  <groupId>com.github.f4b6a3</groupId>
  <artifactId>ulid-creator</artifactId>
  <version>3.1.0</version>
</dependency>
```
See more options in [maven.org](https://search.maven.org/artifact/com.github.f4b6a3/ulid-creator).

### ULID

The ULID is a 128 bit long identifier. The first 48 bits represent the count of milliseconds since Unix Epoch, 1970-01-01. The remaining 80 bits are generated by a secure random number generator.

```java
// Generate a ULID
Ulid ulid = UlidCreator.getUlid();
```

```java
// Generate a ULID with a specific time
Ulid ulid = UlidCreator.getUlid(1234567890);
```

Sequence of ULIDs:

```text
01EX8Y21KBH49ZZCA7KSKH6X1C
01EX8Y21KBJTFK0JV5J20QPQNR
01EX8Y21KBG2CS1V6WQCTVM7K6
01EX8Y21KB8HPZNBP3PTW7HVEY
01EX8Y21KB3HZV38VAPTPAG1TY
01EX8Y21KB9FTEJHPAGAKYG9Z8
01EX8Y21KBQGKGH2SVPQAYEFFC
01EX8Y21KBY17J9WR9KQR8SE7H
01EX8Y21KCVHYSJGVK4HBXDMR9 < millisecond changed
01EX8Y21KC668W3PEDEAGDHMVG
01EX8Y21KC53D2S5ADQ2EST327
01EX8Y21KCPQ3TENMTY1S7HV56
01EX8Y21KC3755QF9STQEV05EB
01EX8Y21KC5ZSHK908GMDK69WE
01EX8Y21KCSGJS8S1FVS06B3SX
01EX8Y21KC6ZBWQ0JBV337R1CN
         ^ look

|---------|--------------|
    time      random
```

### Monotonic ULID

The Monotonic ULID is a 128 bit long identifier. The first 48 bits represent the count of milliseconds since Unix Epoch, 1970-01-01. The remaining 80 bits are generated by a secure random number generator.

The random component is incremented by 1 whenever the current millisecond is equal to the previous one. But when the current millisecond is different, the random component changes to another random value.

```java
// Generate a Monotonic ULID
Ulid ulid = UlidCreator.getMonotonicUlid();
```

```java
// Generate a Monotonic ULID with a specific time
Ulid ulid = UlidCreator.getMonotonicUlid(1234567890);
```

Sequence of Monotonic ULIDs:

```text
01EX8Y7M8MDVX3M3EQG69EEMJW
01EX8Y7M8MDVX3M3EQG69EEMJX
01EX8Y7M8MDVX3M3EQG69EEMJY
01EX8Y7M8MDVX3M3EQG69EEMJZ
01EX8Y7M8MDVX3M3EQG69EEMK0
01EX8Y7M8MDVX3M3EQG69EEMK1
01EX8Y7M8MDVX3M3EQG69EEMK2
01EX8Y7M8MDVX3M3EQG69EEMK3
01EX8Y7M8N1G30CYF2PJR23J2J < millisecond changed
01EX8Y7M8N1G30CYF2PJR23J2K
01EX8Y7M8N1G30CYF2PJR23J2M
01EX8Y7M8N1G30CYF2PJR23J2N
01EX8Y7M8N1G30CYF2PJR23J2P
01EX8Y7M8N1G30CYF2PJR23J2Q
01EX8Y7M8N1G30CYF2PJR23J2R
01EX8Y7M8N1G30CYF2PJR23J2S
         ^ look          ^ look

|---------|--------------|
    time      random
```

### Other usage examples

Create a ULID from a canonical string (26 chars):

```java
Ulid ulid = Ulid.from("0123456789ABCDEFGHJKMNPQRS");
```

Convert a ULID into a canonical string in upper case:

```java
String string = ulid.toUpperCase(); // 0123456789ABCDEFGHJKMNPQRS
```

Convert a ULID into a canonical string in lower case:

```java
String string = ulid.toLowerCase(); // 0123456789abcdefghjkmnpqrs
```

Convert a ULID into a UUID:

```java
UUID uuid = ulid.toUuid(); // 0110c853-1d09-52d8-d73e-1194e95b5f19
```

Convert a ULID into a [RFC-4122](https://tools.ietf.org/html/rfc4122) UUID v4:

```java
UUID uuid = ulid.toRfc4122().toUuid(); // 0110c853-1d09-42d8-973e-1194e95b5f19
                                       //               ^ UUID v4
```

Convert a ULID into a byte array:

```java
byte[] bytes = ulid.toBytes(); // 16 bytes (128 bits)
```

Get the creation instant of a ULID:

```java
Instant instant = ulid.getInstant(); // 2007-02-16T02:13:14.633Z
```

```java
// static method
Instant instant = Ulid.getInstant("0123456789ABCDEFGHJKMNPQRS"); // 2007-02-16T02:13:14.633Z
```

Get the time component of a ULID:

```java
long time = ulid.getTime(); // 1171591994633
```

```java
// static method
long time = Ulid.getTime("0123456789ABCDEFGHJKMNPQRS"); // 1171591994633
```

Get the random component of a ULID:

```java
byte[] random = ulid.getRandom(); // 10 bytes (80 bits)
```

```java
// static method
byte[] random = Ulid.getRandom("0123456789ABCDEFGHJKMNPQRS"); // 10 bytes (80 bits)
```

Use a `UlidFactory` instance with `java.util.Random` to generate ULIDs:

```java
Random random = new Random();
UlidFactory factory = UlidCreator.getDefaultFactory().withRandomGenerator(random::nextBytes);

Ulid ulid = factory.create();
```

Use a `UlidFactory` instance with any random generator you like(*) to generate ULIDs:

```java
import com.github.niceguy.random.AwesomeRandom; // a hypothetical RNG
AwesomeRandom awesomeRandom = new AwesomeRandom();
UlidFactory factory = UlidCreator.getDefaultFactory().withRandomGenerator(awesomeRandom::nextBytes);

Ulid ulid = factory.create();
```

(*) since it provides a void method like `nextBytes(byte[])`.

Benchmark
------------------------------------------------------

This section shows benchmarks comparing `UlidCreator` to `java.util.UUID`.

```
================================================================================
THROUGHPUT (operations/msec)           Mode  Cnt      Score     Error   Units
================================================================================
Throughput.Uuid01_toString              thrpt    5   2876,799 ±  39,938  ops/ms
Throughput.Uuid02_fromString            thrpt    5   1936,569 ±  38,822  ops/ms
Throughput.Uuid03_RandomBased           thrpt    5   2011,774 ±  21,198  ops/ms
--------------------------------------------------------------------------------
Throughput.UlidCreator01_toString       thrpt    5  29487,382 ± 627,808  ops/ms
Throughput.UlidCreator02_fromString     thrpt    5  21194,263 ± 706,398  ops/ms
Throughput.UlidCreator03_Ulid           thrpt    5   2745,123 ±  41,326  ops/ms
Throughput.UlidCreator04_MonotonicUlid  thrpt    5  19542,344 ± 423,271  ops/ms
================================================================================
Total time: 00:09:22
================================================================================
```

System: JVM 8, Ubuntu 20.04, CPU i5-3330, 8G RAM.

See: [uuid-creator-benchmark](https://github.com/fabiolimace/uuid-creator-benchmark)

Other generators
-------------------------------------------
* [UUID Creator](https://github.com/f4b6a3/uuid-creator): for generating UUIDs
* [TSID Creator](https://github.com/f4b6a3/tsid-creator): for generating Time Sortable IDs

