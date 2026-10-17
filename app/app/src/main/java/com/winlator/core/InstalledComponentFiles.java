package com.winlator.core;

import java.io.File;
import java.util.Locale;

/**
 * Only completed, supported component files should be selectable in the UI.
 * A process-killed download can leave .partial files in installed_components.
 * This class does not delete files or modify existing manual selections.
 */
public final class InstalledComponentFiles {
    private InstalledComponentFiles() {}

    public static boolean isSelectable(String componentType, File entry) {
        if (componentType == null || entry == null) return false;
        String name = entry.getName();
        if (name.isEmpty() || name.startsWith(".")) return false;

        if ("adrenotools_driver".equals(componentType)) {
            // Custom AdrenoTools drivers are directories containing manifests.
            return entry.isDirectory();
        }
        if ("soundfont".equals(componentType)) {
            return entry.isFile() && name.toLowerCase(Locale.ROOT).endsWith(".sf2")
                    && name.length() > ".sf2".length();
        }
        String prefix = componentType + "-";
        return entry.isFile() && name.startsWith(prefix) && name.endsWith(".tzst")
                && name.length() > prefix.length() + ".tzst".length();
    }
}
