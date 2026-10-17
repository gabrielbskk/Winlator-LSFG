package com.winlator.core;

import java.util.ArrayList;
import java.util.LinkedHashSet;

/**
 * Validates the remote component index before using entries in URLs or local
 * paths. Version strings are filenames, not trusted directory paths.
 */
public final class ComponentCatalog {
    private ComponentCatalog() {}

    public static boolean isSafeFileName(String type, String filename) {
        if (type == null || filename == null || !type.matches("[a-z][a-z0-9_]{1,40}")
                || filename.length() > 128) return false;
        String prefix = type + "-";
        if (!filename.startsWith(prefix) || !filename.endsWith(".tzst")) return false;
        String version = filename.substring(prefix.length(), filename.length() - ".tzst".length());
        return !version.contains("..") && version.matches("[A-Za-z0-9][A-Za-z0-9._+-]{0,79}");
    }

    /** Keep catalogue order, omit duplicates, comments, blank and unsafe lines. */
    public static String[] validFileNames(String type, String index) {
        if (index == null || index.isEmpty()) return new String[0];
        LinkedHashSet<String> filenames = new LinkedHashSet<>();
        for (String line : index.split("\\r?\\n")) {
            String filename = line.trim();
            if (isSafeFileName(type, filename)) filenames.add(filename);
        }
        return filenames.toArray(new String[0]);
    }
}
