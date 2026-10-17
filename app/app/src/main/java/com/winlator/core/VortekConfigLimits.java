package com.winlator.core;

/**
 * Bounds imported Vortek configuration values before narrowing Java ints to
 * short/byte JNI fields. Normal manual settings within the supported range
 * are preserved; malformed values use the existing defaults.
 */
public final class VortekConfigLimits {
    private VortekConfigLimits() {}

    public static short memoryLimitMB(int requested) {
        return (short)(requested >= 0 && requested <= 12 * 1024 ? requested : 0);
    }

    public static short imageCacheMB(int requested) {
        return (short)(requested >= 1 && requested <= 1024 ? requested : 256);
    }

    public static byte resourceMemoryType(int requested) {
        return (byte)(requested >= 0 && requested <= 3 ? requested : 0);
    }
}
