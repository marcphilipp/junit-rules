package org.junit.rules.ext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SystemPropertiesTest {

	private static final String UNKNOWN_KEY = "unknownKey";
	private static final String KNOWN_KEY = "foo";
	private static final String ORIGINAL_VALUE_OF_KNOWN_KEY = "nonsense";

	public SystemProperties system = new SystemProperties();

	@Before
	public void putKnownKey() {
		System.setProperty(KNOWN_KEY, ORIGINAL_VALUE_OF_KNOWN_KEY);
	}

	@After
	public void removeKnownKey() {
		System.getProperties().remove(KNOWN_KEY);
	}

	@Test
	public void restoresAllKeysInTheEnd() {
		Properties original = new Properties(System.getProperties());
		system.set(KNOWN_KEY, "bar").set(UNKNOWN_KEY, "baz").after();
		Properties modified = new Properties(System.getProperties());
		assertEquals(original, modified);
	}

	@Test
	public void setsSystemProperty() {
		system.set(KNOWN_KEY, "bar");
		assertEquals("bar", System.getProperty(KNOWN_KEY));
	}

	@Test
	public void lastSetWins() {
		system.set(KNOWN_KEY, "bar").set(KNOWN_KEY, "baz");
		assertEquals("baz", System.getProperty(KNOWN_KEY));
	}

	@Test
	public void restorePutsOriginalValueForKnownKey() {
		system.set(KNOWN_KEY, "bar").set(KNOWN_KEY, "baz").restore(KNOWN_KEY);
		assertEquals(ORIGINAL_VALUE_OF_KNOWN_KEY, System.getProperty(KNOWN_KEY));
	}

	@Test
	public void restoreRemovesKeyForUnknownKey() {
		system.set(UNKNOWN_KEY, "bar").set(UNKNOWN_KEY, "baz").restore(UNKNOWN_KEY);
		assertNull(System.getProperty(UNKNOWN_KEY));
	}
}
