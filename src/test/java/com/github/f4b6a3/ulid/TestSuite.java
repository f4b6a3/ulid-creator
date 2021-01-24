package com.github.f4b6a3.ulid;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.github.f4b6a3.ulid.creator.UlidSpecCreatorTest;
import com.github.f4b6a3.ulid.ulid.UlidCreatorDefaultTest;
import com.github.f4b6a3.ulid.ulid.UlidCreatorDefaultStringTest;
import com.github.f4b6a3.ulid.ulid.UlidCreatorMonotonicTest;
import com.github.f4b6a3.ulid.ulid.UlidCreatorMonotonicStringTest;
import com.github.f4b6a3.ulid.util.UlidValidatorTest;
import com.github.f4b6a3.ulid.util.internal.UlidTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	UlidCreatorDefaultTest.class,
	UlidCreatorDefaultStringTest.class,
	UlidCreatorMonotonicTest.class,
	UlidCreatorMonotonicStringTest.class,
	UlidSpecCreatorTest.class,
	UlidValidatorTest.class,
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