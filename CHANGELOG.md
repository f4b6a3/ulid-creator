# Changelog

All notable changes to this project will be documented in this file.

## [Unreleased]

### Fixed

- Fixed typos in `CHANGELOG.md`

## [3.1.0] - 2021-02-13

Adds static methods for extracting information from ULIDs.

### Added 

- Added `CHANGELOG.md`
- Added static method `Ulid.getInstant(String)`
- Added static method `Ulid.getTime(String)`
- Added static method `Ulid.getRandom(String)`

### Changed

- Updated `README.md`
- Updated test cases

## [3.0.1] - 2021-01-30

### Changed

- Renamed subpackage `creator` to `factory`

## [3.0.0] - 2021-01-30

This version breaks compatibility. It is a rewrite from scratch.

Now the `ulid-creator` can generate two types of ULID: default (non-monotonic) and monotonic. Until version 2.3.3 this library only generated monotonic ULIDs.

### Added

- Added class `Ulid`
- Added abstract class `UlidFactory`
- Added class `DefaultUlidFactory`
- Added class `MonotonicUlidFactory`
- Added functional interface `RandomGenerator`
- Added class `DefaultRandomGenerator`
- Added static method `UlidCreator.getUlid(): Ulid`
- Added static method `UlidCreator.getUlid(long): Ulid`
- Added static method `UlidCreator.getMonotonicUlid(): Ulid`
- Added static method `UlidCreator.getMonotonicUlid(long): Ulid`
- Added static method `UlidCreator.getDefaultFactory(): UlidFactory`
- Added static method `UlidCreator.getMonotonicFactory(): UlidFactory`

### Changed

- Rewrote `UlidCreator`
- Rewrote all `README.md`
- Rewrote all test cases

### Removed

- Removed class `UlidStruct`, replaced by `Ulid`
- Removed class `UlidUtil`, use`Ulid` instead
- Removed class `UlidConverter`, use `Ulid` instead
- Removed class `UlidValidator`, use`Ulid` instead
- Removed class `UlidSpecCreator`, replaced by `UlidFactory`
- Removed class `InvalidUlidException`, replaced by `IllegalArgumentException`
- Removed interface `RandomStrategy`, replaced by `RandomGenerator`
- Removed class `DefaultRandomStrategy`, replaced by `DefaultRandomGenerator`
- Removed class `OtherRandomStrategy`
- Removed interface `TimestampStrategy`
- Removed class `DefaultTimestampStrategy`
- Removed class `FixedTimestampStrategy`
- Removed static method `UlidCreator.fromString(String): UUID`
- Removed static method `UlidCreator.toString(UUID): String`
- Removed static method `UlidCreator.getUlid(): UUID`
- Removed static method `UlidCreator.getUlid4(): UUID`
- Removed static method `UlidCreator.getUlidString(): String`
- Removed static method `UlidCreator.getUlidString4(): String`
- Removed static method `UlidCreator.getUlidSpecCreator(): UlidSpecCreator`

## [2.3.3] - 2020-11-16

Optimization and housekeeping.

### Added

- Added test cases

### Changed

- Optimized `UlidSpecCreator`
- Updated `README.md`

### Removed

- Removed unused code

## [2.3.2] - 2020-11-09

Only Optimization.

### Changed

- Optimized `UlidValidator`

## [2.3.1] - 2020-11-08

Added static methods for extracting Unix time.

### Added

- Added static method `UlidUtil.extractUnixMilliseconds(UUID)`
- Added static method `UlidUtil.extractUnixMilliseconds(String)`

### Changed

- Moved UUID v4 generation to `UlidStruct`
- Updated test cases

## [2.3.0] - 2020-11-08

Now it can generate UUIDs and ULIDs compatible with RFC-4122 UUID v4.

### Added

- Added static method `UlidCreator.getUlid4()`
- Added static method `UlidCreator.getUlidString4()`
- Added method `UlidSpecCreator.create4()`
- Added method `UlidSpecCreator.createString4()`

### Changed

- Updated `README.md`
- Updated test cases

## [2.2.0] - 2020-11-08

Now the generation of ULID in canonical string format is 2.5x faster than before.

### Added

- Added `UlidStruct` for internal use
- Added test cases

### Changed

- Optimized `UlidSpecCreator` by using `UlidStruct`
- Optimized `UlidConverter` by using `UlidStruct`
- Optimized `UlidValidator`
- Updated `README.md`
- Updated javadoc

## [2.1.0] - 2020-10-17

Removed the overrun exception because it is extremely unlikely to occur

### Changed

- Updated `README.md`
- Updated test cases
- Updated javadoc

### Removed

- Removed `UlidCreatorException`, used in overruns

## [2.0.0] - 2020-07-04

This version breaks compatibility.

### Added

- Added `RandomStrategy`
- Added `DefaultRandomStrategy`
- Added `OtherRandomStrategy`
- Added test cases

### Changed

- Changed `UlidCreator`
- Renamed `UlidBasedGuidCreator` to `UlidSpecCreator`
- Changed JDK version from 11 to 8 for compatibility with Java 8
- Optimized `UlidConverter.fromString(String)`
- Optimized `UlidConverter.toString(UUID)`
- Optimized `UlidValidator.isValid(String)`
- Updated `README.md`
- Updated `pom.xml`
- Updated test cases
- Updated javadoc

## [1.1.0] - 2020-04-18

### Changed

- Renamed `GuidCreator` to `UlidBasedGuidCreator`
- Changed the overrun exception to be thrown when 2^80 requests occurs within the same msec
- Updated `README.md`
- Updated `pom.xml`
- Updated test cases

### Removed

- Removed unused code

## [1.0.0] - 2020-02-23

Project created as an alternative Java implementation of [ULID spec](https://github.com/ulid/spec).

### Added

- Added `UlidCreator`
- Added `GuidCreator`
- Added `TimestampStrategy`
- Added `DefaultTimestampStrategy`
- Added `FixedTimestampStrategy`
- Added `XorshiftRandom`
- Added `Xorshift128PlusRandom`
- Added `Base32Util`
- Added `ByteUtil`
- Added `FingerprintUtil`
- Added `NetworkData`
- Added `UlidUtil`
- Added `UlidCreatorException`
- Added `README.md`
- Added `pom.xml`
- Added `LICENSE`
- Added test cases

[unreleased]: https://github.com/f4b6a3/ulid-creator/compare/ulid-creator-3.1.0...HEAD
[3.1.0]: https://github.com/f4b6a3/ulid-creator/compare/ulid-creator-3.0.1...ulid-creator-3.1.0
[3.0.1]: https://github.com/f4b6a3/ulid-creator/compare/ulid-creator-3.0.0...ulid-creator-3.0.1
[3.0.0]: https://github.com/f4b6a3/ulid-creator/compare/ulid-creator-2.3.3...ulid-creator-3.0.0
[2.3.3]: https://github.com/f4b6a3/ulid-creator/compare/ulid-creator-2.3.2...ulid-creator-2.3.3
[2.3.2]: https://github.com/f4b6a3/ulid-creator/compare/ulid-creator-2.3.1...ulid-creator-2.3.2
[2.3.1]: https://github.com/f4b6a3/ulid-creator/compare/ulid-creator-2.3.0...ulid-creator-2.3.1
[2.3.0]: https://github.com/f4b6a3/ulid-creator/compare/ulid-creator-2.2.0...ulid-creator-2.3.0
[2.2.0]: https://github.com/f4b6a3/ulid-creator/compare/ulid-creator-2.1.0...ulid-creator-2.2.0
[2.1.0]: https://github.com/f4b6a3/ulid-creator/compare/ulid-creator-2.0.0...ulid-creator-2.1.0
[1.1.0]: https://github.com/f4b6a3/ulid-creator/compare/ulid-creator-1.1.0...ulid-creator-2.0.0
[2.0.0]: https://github.com/f4b6a3/ulid-creator/compare/ulid-creator-1.1.0...ulid-creator-2.0.0
[1.1.0]: https://github.com/f4b6a3/ulid-creator/compare/ulid-creator-1.0.0...ulid-creator-1.1.0
[1.0.0]: https://github.com/f4b6a3/ulid-creator/releases/tag/ulid-creator-1.0.0
