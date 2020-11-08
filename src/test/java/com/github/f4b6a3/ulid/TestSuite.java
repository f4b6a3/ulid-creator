package com.github.f4b6a3.ulid;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.github.f4b6a3.ulid.creator.UlidSpecCreatorTest;
import com.github.f4b6a3.ulid.ulid.UlidCreatorUuidTest;
import com.github.f4b6a3.ulid.ulid.UlidCreatorStringTest;
import com.github.f4b6a3.ulid.util.UlidConverterTest;
import com.github.f4b6a3.ulid.util.UlidUtilTest;
import com.github.f4b6a3.ulid.util.UlidValidatorTest;
import com.github.f4b6a3.ulid.util.internal.UlidStructTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   UlidCreatorUuidTest.class,
   UlidCreatorStringTest.class,
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