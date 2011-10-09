package org.junit.rules.meta;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

public class NameRuleTest {
	
	@Rule
	public TestName test = new TestName();

	@Test
	public void test() {
		assertThat(test.getMethodName(), is("test"));
	}
}