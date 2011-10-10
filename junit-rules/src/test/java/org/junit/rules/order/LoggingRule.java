package org.junit.rules.order;

import static java.lang.System.out;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

public class LoggingRule extends TestWatcher {

	private final String value;

	public LoggingRule(String value) {
		this.value = value;
	}

	@Override
	protected void starting(Description description) {
		out.println("starting " + value);
	}
	
	@Override
	protected void finished(Description description) {
		out.println("finished " + value);
	}
}
