package org.junit.rules.resources;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;

public class SomeTestUsingSystemProperty {

	@Rule
	public ProvideSystemProperty property = new ProvideSystemProperty("someKey", "someValue");

	@Test
	public void test() {
		assertThat(System.getProperty("someKey"), is("someValue"));
	}
}
