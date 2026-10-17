package com.winlator.core;

import java.io.File;
import java.io.IOException;

/** Resolve entries from untrusted custom driver archives inside their root. */
public final class ArchivePathGuard {
    private ArchivePathGuard() {}

    public static boolean safeDriverLabel(String name) {
        return name != null && name.length() <= 80 && !name.contains("..")
                && name.matches("[A-Za-z0-9][A-Za-z0-9._ +()-]{0,79}");
    }

    public static File resolveInside(File root, String relative) throws IOException {
        if (root == null || relative == null || relative.isEmpty()
                || relative.startsWith("/") || relative.indexOf('\\') >= 0
                || relative.indexOf(':') >= 0 || relative.indexOf('\u0000') >= 0)
            throw new IOException("Invalid archive path");
        String name = relative.endsWith("/") ? relative.substring(0, relative.length() - 1) : relative;
        if (name.isEmpty()) throw new IOException("Empty archive path");
        for (String segment : name.split("/", -1))
            if (segment.isEmpty() || segment.equals(".."))
                throw new IOException("Archive path traversal");

        File base = root.getCanonicalFile();
        File child = new File(root, name).getCanonicalFile();
        String prefix = base.getPath() + File.separator;
        if (!child.getPath().startsWith(prefix))
            throw new IOException("Archive entry outside destination");
        return child;
    }

    public static boolean symlinkTargetInside(File root, File link, String target) throws IOException {
        if (target == null || target.isEmpty() || target.startsWith("/")
                || target.indexOf('\\') >= 0 || target.indexOf('\u0000') >= 0) return false;
        File base = root.getCanonicalFile();
        File resolved = new File(link.getParentFile(), target).getCanonicalFile();
        return resolved.getPath().startsWith(base.getPath() + File.separator);
    }
}
