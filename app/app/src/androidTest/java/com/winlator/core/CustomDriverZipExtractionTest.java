package com.winlator.core;

import android.content.Context;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.Assert.*;

/** Run on an Android ARM64 device; CI currently compiles but cannot execute it. */
@RunWith(AndroidJUnit4.class)
public class CustomDriverZipExtractionTest {
    private void writeZip(File archive, String member) throws Exception {
        try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(archive))) {
            out.putNextEntry(new ZipEntry(member));
            out.write("driver-data".getBytes(StandardCharsets.UTF_8));
            out.closeEntry();
        }
    }

    @Test public void regularNestedDriverFileExtractsUnderPrivateDirectory() throws Exception {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        File archive = File.createTempFile("aurora-driver", ".zip", context.getCacheDir());
        File output = new File(context.getCacheDir(), "aurora-extract-safe-" + System.nanoTime());
        try {
            writeZip(archive, "libs/libvulkan.so");
            assertTrue(ZipUtils.extract(archive, output));
            assertArrayEquals("driver-data".getBytes(StandardCharsets.UTF_8),
                    Files.readAllBytes(new File(output, "libs/libvulkan.so").toPath()));
        } finally {
            FileUtils.delete(output);
            archive.delete();
        }
    }

    @Test public void zipTraversalNeverWritesOutsideExtractionDirectory() throws Exception {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        File archive = File.createTempFile("aurora-driver", ".zip", context.getCacheDir());
        String name = "aurora-extract-escape-" + System.nanoTime() + ".so";
        File output = new File(context.getCacheDir(), "aurora-extract-root-" + System.nanoTime());
        File escaped = new File(context.getCacheDir(), name);
        try {
            writeZip(archive, "../" + name);
            assertFalse(ZipUtils.extract(archive, output));
            assertFalse(escaped.exists());
        } finally {
            FileUtils.delete(output);
            archive.delete();
            escaped.delete();
        }
    }
}
