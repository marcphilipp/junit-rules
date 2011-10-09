package org.junit.rules.resources;

import org.junit.rules.ExternalResource;

public class ProvideSystemProperty extends ExternalResource {

	private final String key, value;
	private String oldValue;

	public ProvideSystemProperty(String key, String value) {
		this.key = key;
		this.value = value;
	}

	@Override
	protected void before() {
		oldValue = System.getProperty(key);
		System.setProperty(key, value);
	}

	@Override
	protected void after() {
		if (oldValue == null) {
			System.clearProperty(key);
		} else {
			System.setProperty(key, oldValue);
		}
	}
}