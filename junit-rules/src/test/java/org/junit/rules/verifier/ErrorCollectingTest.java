package org.junit.rules.verifier;

import static org.hamcrest.CoreMatchers.is;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

public class ErrorCollectingTest {

	@Rule
	public ErrorCollector collector = new ErrorCollector();

	@Test
	public void test() {
		collector.checkThat(1 + 1, is(3));
		collector.addError(new Exception("something went wrong"));
	}
}