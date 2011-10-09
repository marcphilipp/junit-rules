package org.junit.rules.exceptions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

public class ExpectedExceptionWithoutRule {

	int[] threeNumbers = { 1, 2, 3 };

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void exception() {
		threeNumbers[3] = 4;
	}

	@Test
	public void exceptionWithMessage() {
		try {
			threeNumbers[3] = 4;
			fail("ArrayIndexOutOfBoundsException expected");
		} catch (ArrayIndexOutOfBoundsException expected) {
			assertEquals("3", expected.getMessage());
		}
	}
}