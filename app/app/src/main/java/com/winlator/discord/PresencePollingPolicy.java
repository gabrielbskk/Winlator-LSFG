package com.winlator.discord;

/**
 * Keep SDK callback processing responsive while avoiding ten wakeups per second
 * for an already-published Rich Presence activity.
 */
final class PresencePollingPolicy {
    private PresencePollingPolicy() {}

    static long delayMillis(int result, boolean hasGame) {
        // Results: 0=idle, 1=pending, 2=published, 3=retrying.
        if (!hasGame) return 1000;
        return result == 1 ? 250 : 1000;
    }
}
