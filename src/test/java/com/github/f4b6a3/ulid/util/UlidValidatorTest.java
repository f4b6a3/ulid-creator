package com.github.f4b6a3.ulid.util;

import static org.junit.Assert.*;

import org.junit.Test;

import com.github.f4b6a3.ulid.util.UlidValidator;

public class UlidValidatorTest {

	@Test
	public void testIsValid() {

		String ulid = null; // Null
		assertFalse("Null ULID should be invalid.", UlidValidator.isValid(ulid));

		ulid = ""; // length: 0
		assertFalse("ULID with empty string should be invalid .", UlidValidator.isValid(ulid));

		ulid = "0123456789ABCDEFGHJKMNPQRS"; // All upper case
		assertTrue("ULID in upper case should valid.", UlidValidator.isValid(ulid));

		ulid = "0123456789abcdefghjklmnpqr"; // All lower case
		assertTrue("ULID in lower case should be valid.", UlidValidator.isValid(ulid));

		ulid = "0123456789AbCdEfGhJkMnPqRs"; // Mixed case
		assertTrue("Ulid in upper and lower case should valid.", UlidValidator.isValid(ulid));

		ulid = "0123456789ABCDEFGHJKLMNPQ"; // length: 25
		assertFalse("ULID length lower than 26 should be invalid.", UlidValidator.isValid(ulid));

		ulid = "0123456789ABCDEFGHJKMNPQZZZ"; // length: 27
		assertFalse("ULID length greater than 26 should be invalid.", UlidValidator.isValid(ulid));

		ulid = "u123456789ABCDEFGHJKMNPQRS"; // Letter u
		assertFalse("ULID with 'u' or 'U' should be invalid.", UlidValidator.isValid(ulid));

		ulid = "#123456789ABCDEFGHJKMNPQRS"; // Special char
		assertFalse("ULID with special chars should be invalid.", UlidValidator.isValid(ulid));

		ulid = "01234-56789-ABCDEFGHJKM---NPQRS"; // Hyphens
		assertTrue("ULID with hiphens should be valid.", UlidValidator.isValid(ulid));

		ulid = "8ZZZZZZZZZABCDEFGHJKMNPQRS"; // timestamp > (2^48)-1
		assertFalse("ULID with timestamp greater than (2^48)-1 should be invalid.", UlidValidator.isValid(ulid));
	}
}
