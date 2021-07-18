package com.github.f4b6a3.ulid;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.github.f4b6a3.ulid.factory.DefaultFactoryTest;
import com.github.f4b6a3.ulid.factory.MonotonicFactoryTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	MonotonicFactoryTest.class,
	DefaultFactoryTest.class,
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