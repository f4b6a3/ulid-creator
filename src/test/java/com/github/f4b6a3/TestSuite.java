package com.github.f4b6a3;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.github.f4b6a3.ulid.UlidCreatorTest;
import com.github.f4b6a3.ulid.guid.GuidCreatorTest;
import com.github.f4b6a3.ulid.random.NaiveRandomTest;
import com.github.f4b6a3.ulid.timestamp.DefaultTimestampStrategyTest;
import com.github.f4b6a3.ulid.util.Base32UtilTest;
import com.github.f4b6a3.ulid.util.ByteUtilTest;
import com.github.f4b6a3.ulid.util.UlidUtilTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   DefaultTimestampStrategyTest.class,
   ByteUtilTest.class,
   NaiveRandomTest.class,
   GuidCreatorTest.class,
   Base32UtilTest.class,
   UlidUtilTest.class,
   UlidCreatorTest.class,
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