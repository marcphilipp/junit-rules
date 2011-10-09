package org.junit.rules.watcher;

import static org.junit.Assert.fail;

import org.junit.Rule;
import org.junit.Test;

public class FailingTestThatBeeps {
	
	@Rule public BeepOnFailure beep = new BeepOnFailure();

	@Test
	public void test() {
		fail();
	}
}
