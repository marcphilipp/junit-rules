package org.junit.rules.timeout;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

public class GlobalTimeout {

	@Rule
	public Timeout timeout = new Timeout(20);

	@Test
	public void firstTest() {
		while (true) {
		}
	}

	@Test
	public void secondTest() {
		for (;;) {
		}
	}
}