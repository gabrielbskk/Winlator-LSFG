package com.winlator.core;

/** Versioned baseline policy, not a hardware certification matrix. */
public final class TurnipPolicy {
    public static final int REVISION = 1;
    private static final class Row {
        final int first, last;
        final String version, reason;
        Row(int first, int last, String version, String reason) {
            this.first = first; this.last = last; this.version = version; this.reason = reason;
        }
    }
    // Keep generations separate so device evidence can change one without the other.
    // Both retain the existing baseline; no evidence currently justifies switching it.
    private static final Row[] TABLE = {
        new Row(600, 699, "26.1.0", "A6xx existing baseline; hardware validation pending"),
        new Row(700, 799, "26.1.0", "A7xx existing baseline; hardware validation pending")
    };
    private TurnipPolicy() {}
    private static Row find(String renderer) {
        int model = GpuCompatibility.adrenoModelId(renderer);
        for (Row row : TABLE) if (model >= row.first && model <= row.last) return row;
        return null;
    }
    public static String recommendedVersion(String renderer) {
        Row row = find(renderer);
        return row == null ? "" : row.version;
    }
    public static String reason(String renderer) {
        Row row = find(renderer);
        return row == null ? "Outside validated policy range; conservative system path" : row.reason;
    }
    public static String resolveVersion(String renderer, String manualVersion) {
        if (manualVersion != null && !manualVersion.isEmpty() && !manualVersion.equals("auto"))
            return manualVersion;
        return recommendedVersion(renderer);
    }
}
