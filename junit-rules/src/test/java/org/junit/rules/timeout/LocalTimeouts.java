package org.junit.rules.timeout;

import org.junit.Test;

public class LocalTimeouts {

	@Test(timeout = 20)
	public void firstTest() {
		while (true) {
		}
	}

	@Test(timeout = 20)
	public void secondTest() {
		for (;;) {
		}
	}
}