package com.winlator.core;

import android.content.Context;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public abstract class ZipUtils {
    private static void addFile(ZipOutputStream zip, File file, String entryName) {
        try {
            ZipEntry entry = new ZipEntry(entryName);
            entry.setTime(file.lastModified());
            zip.putNextEntry(entry);

            try (BufferedInputStream inStream = new BufferedInputStream(new FileInputStream(file), StreamUtils.BUFFER_SIZE)) {
                StreamUtils.copy(inStream, zip);
            }
        }
        catch (Exception e) {}
    }

    private static void addDirectory(ZipOutputStream zip, File folder, String basePath) throws IOException {
        File[] files = folder.listFiles();
        if (files == null) return;
        for (File file : files) {
            if (FileUtils.isSymlink(file)) continue;
            if (file.isDirectory()) {
                String entryName = basePath+file.getName() + "/";
                ZipEntry entry = new ZipEntry(entryName);
                entry.setTime(file.lastModified());
                zip.putNextEntry(entry);
                addDirectory(zip, file, entryName);
            }
            else addFile(zip, file, basePath+file.getName());
        }
    }

    public static void compress(File file, File destination) {
        compress(new File[]{file}, destination);
    }

    public static void compress(File[] files, File destination) {
        try {
            try (ZipOutputStream zip = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(destination), StreamUtils.BUFFER_SIZE))) {
                for (File file : files) {
                    if (FileUtils.isSymlink(file)) continue;
                    if (file.isDirectory()) {
                        String basePath = file.getName() + "/";
                        zip.putNextEntry(new ZipEntry(basePath));
                        addDirectory(zip, file, basePath);
                    }
                    else addFile(zip, file, file.getName());
                }
            }
        }
        catch (IOException e) {}
    }

    private static final long MAX_DRIVER_ARCHIVE_BYTES = 1024L * 1024 * 1024;
    private static final long MAX_DRIVER_ENTRY_BYTES = 512L * 1024 * 1024;
    private static final int MAX_DRIVER_ENTRIES = 4096;
    private static final int MAX_DRIVER_MANIFEST_BYTES = 2 * 1024 * 1024;

    public static boolean extract(File source, File destination) {
        // Custom driver ZIP files come from user storage. Reject path traversal,
        // unsafe symlinks and nested paths through symlinks before writing.
        if (source == null || !source.isFile() || destination == null) return false;
        try (ZipFile zipFile = new ZipFile(source)) {
            Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();
            long extractedBytes = 0;
            int count = 0;
            while (entries.hasMoreElements()) {
                if (++count > MAX_DRIVER_ENTRIES) return false;
                ZipArchiveEntry entry = entries.nextElement();
                if (entry.getSize() > MAX_DRIVER_ENTRY_BYTES) return false;
                File file = ArchivePathGuard.resolveInside(destination, entry.getName());

                if (entry.isDirectory()) {
                    if (!file.isDirectory() && !file.mkdirs()) return false;
                }
                else {
                    File parent = file.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) return false;
                    if (entry.isUnixSymlink()) {
                        String linkTarget = zipFile.getUnixSymlink(entry);
                        if (!ArchivePathGuard.symlinkTargetInside(destination, file, linkTarget))
                            return false;
                        FileUtils.symlink(linkTarget, file.getAbsolutePath());
                    }
                    else {
                        try (InputStream inStream = zipFile.getInputStream(entry);
                             BufferedOutputStream outStream = new BufferedOutputStream(
                                     new FileOutputStream(file), StreamUtils.BUFFER_SIZE)) {
                            byte[] buffer = new byte[StreamUtils.BUFFER_SIZE];
                            long entryBytes = 0;
                            int length;
                            while ((length = inStream.read(buffer)) != -1) {
                                entryBytes += length;
                                extractedBytes += length;
                                if (entryBytes > MAX_DRIVER_ENTRY_BYTES || extractedBytes > MAX_DRIVER_ARCHIVE_BYTES)
                                    return false;
                                outStream.write(buffer, 0, length);
                            }
                        }
                    }
                }
                FileUtils.chmod(file, 0771);
            }
            return true;
        }
        catch (Exception error) {
            android.util.Log.w("ZipUtils", "Unsafe or invalid component ZIP", error);
            return false;
        }
    }

    public static void extract(Context context, String assetFile, File destination) {
        try (ZipInputStream zip = new ZipInputStream(context.getAssets().open(assetFile))) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                File file = new File(destination, entry.getName());

                if (entry.isDirectory()) {
                    if (!file.isDirectory()) file.mkdirs();
                }
                else {
                    try (BufferedOutputStream outStream = new BufferedOutputStream(new FileOutputStream(file), StreamUtils.BUFFER_SIZE)) {
                        StreamUtils.copy(zip, outStream);
                        zip.closeEntry();
                    }
                }

                FileUtils.chmod(file, 0771);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Read a small manifest without leaking a ZipFile descriptor on early return. */
    public static byte[] read(File source, String localPath) {
        if (source == null || !source.isFile() || localPath == null || localPath.isEmpty())
            return null;
        boolean suffix = localPath.startsWith("*");
        boolean prefix = !suffix && localPath.endsWith("*");
        String match = localPath.replace("*", "");

        try (ZipFile zipFile = new ZipFile(source)) {
            Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();
            int examined = 0;
            while (entries.hasMoreElements()) {
                if (++examined > MAX_DRIVER_ENTRIES) return null;
                ZipArchiveEntry entry = entries.nextElement();
                String name = entry.getName();
                boolean matches = suffix ? name.endsWith(match)
                        : prefix ? name.startsWith(match) : name.equals(match);
                if (!matches || entry.isDirectory() || entry.isUnixSymlink()) continue;
                if (entry.getSize() > MAX_DRIVER_MANIFEST_BYTES) return null;

                try (InputStream stream = zipFile.getInputStream(entry);
                     ByteArrayOutputStream output = new ByteArrayOutputStream()) {
                    byte[] buffer = new byte[8192];
                    int length;
                    while ((length = stream.read(buffer)) != -1) {
                        if (output.size() + (long)length > MAX_DRIVER_MANIFEST_BYTES) return null;
                        output.write(buffer, 0, length);
                    }
                    return output.toByteArray();
                }
            }
        }
        catch (Exception error) {
            android.util.Log.w("ZipUtils", "Unable to read custom driver manifest", error);
        }
        return null;
    }
}
