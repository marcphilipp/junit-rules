package org.junit.rules.multiple;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestName;
import org.junit.rules.TestRule;
import org.junit.rules.watcher.BeepOnFailure;

public class CombiningMultipleRules {

	@Rule
	public TestRule beep = new BeepOnFailure();
	
	@Rule
	public ExpectedException exceptions = ExpectedException.none();
	
	@Rule
	public TestName test = new TestName();

	@Test
	public void test() {
		exceptions.expect(IllegalArgumentException.class);
		throw new RuntimeException("Hello from " + test.getMethodName());
	}
}
