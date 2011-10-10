package org.junit.rules.order;

import static java.lang.System.out;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

public class Order {
	
	@ClassRule
	public static TestRule classRule = new LoggingRule("class rule");

	@Rule
	public TestRule methodRule = new LoggingRule("method rule");

	@BeforeClass
	public static void beforeClass() {
		out.println("beforeClass()");
	}

	@Before
	public void before() {
		out.println("before()");
	}

	@Test
	public void test1() {
		out.println("test1()");
	}

	@Test
	public void test2() {
		out.println("test2()");
	}

	@After
	public void after() {
		out.println("after()");
	}

	@AfterClass
	public static void afterClass() {
		out.println("afterClass()");
	}
}