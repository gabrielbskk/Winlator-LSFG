package com.winlator.core;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Test;
import static org.junit.Assert.*;

/** Offline regression coverage: downloaded drivers survive network failures. */
public class AtomicDownloadTest {
    private static byte[] bytes(String data) {
        return data.getBytes(StandardCharsets.UTF_8);
    }

    @Test public void successfulDownloadReplacesTheOldFile() throws Exception {
        File dir = Files.createTempDirectory("aurora-driver-test").toFile();
        try {
            File driver = new File(dir, "turnip-26.1.0.tzst");
            Files.write(driver.toPath(), bytes("old driver"));
            assertTrue(AtomicDownload.write(new ByteArrayInputStream(bytes("new driver")),
                    driver, bytes("new driver").length, new AtomicBoolean(false), null));
            assertArrayEquals(bytes("new driver"), Files.readAllBytes(driver.toPath()));
            assertEquals(1, dir.listFiles().length);
        } finally {
            for (File child : dir.listFiles()) child.delete();
            dir.delete();
        }
    }

    @Test public void incompleteTransferCannotEraseAnInstalledDriver() throws Exception {
        File dir = Files.createTempDirectory("aurora-driver-test").toFile();
        try {
            File driver = new File(dir, "turnip-25.0.0.tzst");
            Files.write(driver.toPath(), bytes("known-good"));
            assertFalse(AtomicDownload.write(new ByteArrayInputStream(bytes("short")),
                    driver, 1000, new AtomicBoolean(false), null));
            assertArrayEquals(bytes("known-good"), Files.readAllBytes(driver.toPath()));
            assertEquals(1, dir.listFiles().length);
        } finally {
            for (File child : dir.listFiles()) child.delete();
            dir.delete();
        }
    }

    @Test public void cancelledTransferLeavesInstalledVersionIntact() throws Exception {
        File dir = Files.createTempDirectory("aurora-driver-test").toFile();
        try {
            File driver = new File(dir, "turnip-24.3.4.1.tzst");
            Files.write(driver.toPath(), bytes("original"));
            AtomicBoolean cancel = new AtomicBoolean(true);
            assertFalse(AtomicDownload.write(new ByteArrayInputStream(bytes("replacement")),
                    driver, 11, cancel, null));
            assertArrayEquals(bytes("original"), Files.readAllBytes(driver.toPath()));
            assertEquals(1, dir.listFiles().length);
        } finally {
            for (File child : dir.listFiles()) child.delete();
            dir.delete();
        }
    }

    @Test public void networkIOExceptionPreservesOldDownloadAndCleansStaging() throws Exception {
        File dir = Files.createTempDirectory("aurora-driver-test").toFile();
        try {
            File driver = new File(dir, "turnip-26.2.1.1.tzst");
            Files.write(driver.toPath(), bytes("previous"));
            InputStream broken = new InputStream() {
                private int calls = 0;
                @Override public int read() throws IOException {
                    if (calls++ == 0) return 66;
                    throw new IOException("Simulated connection reset");
                }
            };
            assertFalse(AtomicDownload.write(broken, driver, -1, new AtomicBoolean(false), null));
            assertArrayEquals(bytes("previous"), Files.readAllBytes(driver.toPath()));
            assertEquals(1, dir.listFiles().length);
        } finally {
            for (File child : dir.listFiles()) child.delete();
            dir.delete();
        }
    }
}
