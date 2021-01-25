package com.github.f4b6a3.ulid;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.github.f4b6a3.ulid.creator.MonotonicUlidSpecCreatorTest;
import com.github.f4b6a3.ulid.creator.UlidSpecCreatorTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	MonotonicUlidSpecCreatorTest.class,
	UlidSpecCreatorTest.class,
	UlidTest.class,
})

/**
 * 
 * It bundles all JUnit test cases.
 * 
 * Also see {@link UniquenesTest}. 
 *
 */
public class TestSuite {
}