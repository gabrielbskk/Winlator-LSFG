package com.winlator.core;

import org.junit.Test;
import java.util.concurrent.TimeUnit;
import static org.junit.Assert.*;

public class GpuProbeGateTest {
    @Test public void sharesOneProbeAcrossSimultaneousCallers() throws Exception {
        GpuProbeGate gate = new GpuProbeGate(5000);
        GpuProbeGate.Attempt first = gate.acquire(100);
        GpuProbeGate.Attempt second = gate.acquire(101);
        assertTrue(first.owner);
        assertFalse(second.owner);
        assertSame(first.completed, second.completed);
        assertEquals(1, second.completed.getCount());
        gate.complete(second, 102);
        assertEquals(1, first.completed.getCount());
        gate.complete(first, 200);
        assertTrue(second.completed.await(1, TimeUnit.MILLISECONDS));
    }

    @Test public void failureCanRetryAfterCooldownWithoutSpawningRepeatedWorkers() {
        GpuProbeGate gate = new GpuProbeGate(5000);
        GpuProbeGate.Attempt first = gate.acquire(100);
        gate.complete(first, 150);
        assertFalse(gate.acquire(5149).owner);
        assertNull(gate.acquire(5149).completed);
        GpuProbeGate.Attempt retried = gate.acquire(5150);
        assertTrue(retried.owner);
        assertNotSame(first.completed, retried.completed);
        gate.complete(first, 5200);
        assertEquals(1, retried.completed.getCount());
        gate.complete(retried, 5200);
        assertEquals(0, retried.completed.getCount());
    }

    @Test public void hungProbeDoesNotLaunchMoreThreads() {
        GpuProbeGate gate = new GpuProbeGate(5000);
        GpuProbeGate.Attempt first = gate.acquire(100);
        GpuProbeGate.Attempt afterTimeout = gate.acquire(20_000);
        assertFalse(afterTimeout.owner);
        assertSame(first.completed, afterTimeout.completed);
    }

    @Test public void negativeCooldownIsNotAllowed() {
        try {
            new GpuProbeGate(-1);
            fail("Expected invalid cooldown to be rejected");
        } catch (IllegalArgumentException expected) {
            assertEquals("cooldownMs", expected.getMessage());
        }
    }
}
