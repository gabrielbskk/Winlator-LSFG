package com.winlator.core;

/**
 * Manual Turnip version selection with a fixed migration fallback for legacy
 * containers. No GPU-based or model-specific automatic version policy.
 */
public final class TurnipVersionSelection {
    private TurnipVersionSelection() {}

    /**
     * Preserve any explicitly saved version. Older configurations sometimes
     * used an absent version or the token "auto"; those are mapped to the
     * bundled version without consulting the GPU or altering saved data.
     */
    public static String resolve(String savedVersion) {
        if (savedVersion == null) return DefaultVersion.TURNIP;
        String candidate = savedVersion.trim();
        return candidate.isEmpty() || candidate.equalsIgnoreCase("auto")
                ? DefaultVersion.TURNIP : candidate;
    }
}
