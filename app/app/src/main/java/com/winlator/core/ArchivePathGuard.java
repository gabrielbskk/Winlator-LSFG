package com.winlator.core;

import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;

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

    /** Android APK is ARM64-only. Refuse non-ELF, 32-bit and wrong-CPU Vulkan libraries. */
    public static boolean isArm64SharedLibrary(File library) throws IOException {
        if (library == null || !library.isFile()) return false;
        byte[] header = new byte[20];
        try (FileInputStream input = new FileInputStream(library)) {
            int read = 0;
            while (read < header.length) {
                int n = input.read(header, read, header.length - read);
                if (n < 0) return false;
                read += n;
            }
        }
        // ELF64 little-endian, ET_DYN (3), EM_AARCH64 (183).
        return (header[0] & 255) == 0x7f && header[1] == 'E'
                && header[2] == 'L' && header[3] == 'F'
                && header[4] == 2 && header[5] == 1
                && header[16] == 3 && header[17] == 0
                && (header[18] & 255) == 183 && header[19] == 0;
    }

    public static boolean symlinkTargetInside(File root, File link, String target) throws IOException {
        if (target == null || target.isEmpty() || target.startsWith("/")
                || target.indexOf('\\') >= 0 || target.indexOf('\u0000') >= 0) return false;
        File base = root.getCanonicalFile();
        File resolved = new File(link.getParentFile(), target).getCanonicalFile();
        return resolved.getPath().startsWith(base.getPath() + File.separator);
    }
}
