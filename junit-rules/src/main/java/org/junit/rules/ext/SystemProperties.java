package org.junit.rules.ext;

import java.util.HashMap;
import java.util.Map;

import org.junit.rules.ExternalResource;

public class SystemProperties extends ExternalResource {

	private final Map<String, String> values = new HashMap<String, String>();

	public SystemProperties set(String key, String value) {
		backup(key);
		put(key, value);
		return this;
	}

	public SystemProperties clear(String key) {
		set(key, null);
		return this;
	}

	@Override
	protected void after() {
		restoreAll();
	}

	public void restoreAll() {
		for (String key : values.keySet()) {
			restore(key);
		}
	}

	public void restore(String key) {
		put(key, values.get(key));
	}

	private void backup(String key) {
		if (values.containsKey(key)) {
			return;
		}
		values.put(key, System.getProperty(key));
	}

	private void put(String key, String value) {
		if (value == null) {
			System.clearProperty(key);
		} else {
			System.setProperty(key, value);
		}
	}
}
