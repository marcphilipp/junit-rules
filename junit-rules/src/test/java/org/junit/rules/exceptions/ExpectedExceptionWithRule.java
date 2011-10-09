package org.junit.rules.exceptions;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ExpectedExceptionWithRule {

	int[] threeNumbers = { 1, 2, 3 };

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void exception() {
		thrown.expect(ArrayIndexOutOfBoundsException.class);
		threeNumbers[3] = 4;
	}

	@Test
	public void exceptionWithMessage() {
		thrown.expect(ArrayIndexOutOfBoundsException.class);
		thrown.expectMessage("3");
		threeNumbers[3] = 4;
	}
}