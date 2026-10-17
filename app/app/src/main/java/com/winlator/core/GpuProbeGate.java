package com.winlator.core;

import java.util.concurrent.CountDownLatch;

/**
 * Restricts GPU identity detection to one EGL/Vulkan probe at a time.
 * A failed or completed probe can retry after a short cooldown, but a hung
 * native driver call cannot spawn unlimited probe threads.
 *
 * All times are monotonic milliseconds (Android SystemClock.elapsedRealtime).
 */
public final class GpuProbeGate {
    public static final class Attempt {
        public final CountDownLatch completed;
        public final boolean owner;

        private Attempt(CountDownLatch completed, boolean owner) {
            this.completed = completed;
            this.owner = owner;
        }
    }

    private final long cooldownMs;
    private CountDownLatch inFlight;
    private long retryAfterMs;

    public GpuProbeGate(long cooldownMs) {
        if (cooldownMs < 0) throw new IllegalArgumentException("cooldownMs");
        this.cooldownMs = cooldownMs;
    }

    public synchronized Attempt acquire(long nowMs) {
        if (inFlight != null) return new Attempt(inFlight, false);
        if (nowMs < retryAfterMs) return new Attempt(null, false);
        inFlight = new CountDownLatch(1);
        return new Attempt(inFlight, true);
    }

    public synchronized void complete(Attempt attempt, long nowMs) {
        if (attempt == null || !attempt.owner || attempt.completed != inFlight) return;
        retryAfterMs = nowMs > Long.MAX_VALUE - cooldownMs ? Long.MAX_VALUE : nowMs + cooldownMs;
        inFlight = null;
        attempt.completed.countDown();
    }
}
