package com.github.f4b6a3.ulid;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.github.f4b6a3.ulid.factory.DefaultUlidFactoryTest;
import com.github.f4b6a3.ulid.factory.MonotonicUlidFactoryTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	MonotonicUlidFactoryTest.class,
	DefaultUlidFactoryTest.class,
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