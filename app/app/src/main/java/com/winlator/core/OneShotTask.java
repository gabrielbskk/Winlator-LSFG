package com.winlator.core;

/** A finite operation must not leave an executor worker waiting indefinitely. */
public final class OneShotTask {
    private OneShotTask() {}
    public static void execute(Runnable task) {
        new Thread(task, "aurora-one-shot").start();
    }
}
