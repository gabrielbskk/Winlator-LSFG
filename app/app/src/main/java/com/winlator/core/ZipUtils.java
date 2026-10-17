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

    public static boolean extract(File source, File destination) {
        // Custom driver ZIP files come from user storage. Reject path traversal,
        // unsafe symlinks and nested paths through symlinks before writing.
        if (source == null || !source.isFile() || destination == null) return false;
        try (ZipFile zipFile = new ZipFile(source)) {
            Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();
            while (entries.hasMoreElements()) {
                ZipArchiveEntry entry = entries.nextElement();
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
                            if (!StreamUtils.copy(inStream, outStream)) return false;
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

    public static byte[] read(File source, String localPath) {
        try {
            boolean pathIsPrefix = false;
            boolean pathIsSuffix = false;

            if (localPath.startsWith("*")) {
                pathIsSuffix = true;
            }
            else if (localPath.endsWith("*")) {
                pathIsPrefix = true;
            }

            localPath = localPath.replace("*", "");
            ByteArrayOutputStream dataOutputStream = new ByteArrayOutputStream();

            ZipFile zipFile = new ZipFile(source);
            Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();
            while (entries.hasMoreElements()) {
                ZipArchiveEntry entry = entries.nextElement();
                String entryName = entry.getName();
                boolean match = pathIsSuffix ? entryName.endsWith(localPath) : (pathIsPrefix ? entryName.startsWith(localPath) : entryName.equals(localPath));

                if (match && !entry.isDirectory() && !entry.isUnixSymlink()) {
                    try (InputStream inStream = zipFile.getInputStream(entry);
                        BufferedOutputStream outStream = new BufferedOutputStream(dataOutputStream, StreamUtils.BUFFER_SIZE)) {
                        if (!StreamUtils.copy(inStream, outStream)) return null;
                    }

                    return dataOutputStream.toByteArray();
                }
            }

            zipFile.close();
            return null;
        }
        catch (Exception e) {
            return null;
        }
    }
}