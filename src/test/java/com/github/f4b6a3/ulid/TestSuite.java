package com.github.f4b6a3.ulid;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.github.f4b6a3.ulid.creator.UlidSpecCreatorTest;
import com.github.f4b6a3.ulid.ulid.UlidCreatorTest;
import com.github.f4b6a3.ulid.util.UlidConverterTest;
import com.github.f4b6a3.ulid.util.UlidUtilTest;
import com.github.f4b6a3.ulid.util.UlidValidatorTest;
import com.github.f4b6a3.ulid.util.internal.UlidStructTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   UlidCreatorTest.class,
   UlidSpecCreatorTest.class,
   UlidConverterTest.class,
   UlidUtilTest.class,
   UlidValidatorTest.class,
   UlidStructTest.class,
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