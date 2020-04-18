package com.github.f4b6a3.ulid;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.github.f4b6a3.ulid.creator.UlidBasedGuidCreatorTest;
import com.github.f4b6a3.ulid.ulid.UlidCreatorTest;
import com.github.f4b6a3.ulid.util.UlidConverterTest;
import com.github.f4b6a3.ulid.util.UlidUtilTest;
import com.github.f4b6a3.ulid.util.UlidValidatorTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   UlidCreatorTest.class,
   UlidBasedGuidCreatorTest.class,
   UlidConverterTest.class,
   UlidUtilTest.class,
   UlidValidatorTest.class,
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