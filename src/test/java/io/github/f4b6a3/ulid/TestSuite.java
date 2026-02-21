package io.github.f4b6a3.ulid;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	UlidFactoryMonotonicTest.class,
	UlidFactoryDefaultfTest.class,
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
