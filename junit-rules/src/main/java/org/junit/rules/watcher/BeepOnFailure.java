package org.junit.rules.watcher;

import java.awt.Toolkit;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

public class BeepOnFailure extends TestWatcher {

    @Override protected void failed(Throwable e, Description description) {
        Toolkit.getDefaultToolkit().beep();
    }
}