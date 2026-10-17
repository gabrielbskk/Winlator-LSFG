package com.winlator.discord;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class PresencePollingPolicyTest {
    @Test public void pendingCallbacksArePolledMoreOften() {
        assertEquals(250L, PresencePollingPolicy.delayMillis(1, true));
    }

    @Test public void publishedOrRetryingPresenceUsesLowOverheadCadence() {
        assertEquals(1000L, PresencePollingPolicy.delayMillis(2, true));
        assertEquals(1000L, PresencePollingPolicy.delayMillis(3, true));
        assertEquals(1000L, PresencePollingPolicy.delayMillis(0, true));
    }

    @Test public void idleClientDoesNotSpin() {
        assertEquals(1000L, PresencePollingPolicy.delayMillis(0, false));
        assertEquals(1000L, PresencePollingPolicy.delayMillis(1, false));
    }
}
