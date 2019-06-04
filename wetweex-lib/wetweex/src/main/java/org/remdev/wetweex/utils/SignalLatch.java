package org.remdev.wetweex.utils;

import java.util.concurrent.CountDownLatch;

public class SignalLatch extends CountDownLatch {

    /**
     * Constructs a {@code CountDownLatch} initialized with the single count.
     *
     * Can be used to await some operation to be completed
     */
    public SignalLatch() {
        super(1);
    }
}
