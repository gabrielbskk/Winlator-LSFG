package com.winlator.core;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** Offline regression tests for cached Windows installer integrity. */
public class FirefoxInstallerIntegrityTest {
    @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

    // Standard SHA-256 test vector for the ASCII string "abc".
    private static final String ABC_SHA256 =
        "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad";

    @Test public void acceptsFileOnlyWhenSizeAndDigestMatch() throws Exception {
        File installer = temporaryFolder.newFile("Firefox-Setup-test.exe");
        Files.write(installer.toPath(), "abc".getBytes(StandardCharsets.US_ASCII));

        assertTrue(FirefoxInstaller.matchesExpectedHash(installer, 3, ABC_SHA256));
        assertFalse(FirefoxInstaller.matchesExpectedHash(installer, 4, ABC_SHA256));
    }

    @Test public void rejectsTamperedInstallerEvenWithIdenticalByteCount() throws Exception {
        File installer = temporaryFolder.newFile("Firefox-Setup-corrupt.exe");
        Files.write(installer.toPath(), "abd".getBytes(StandardCharsets.US_ASCII));

        assertFalse(FirefoxInstaller.matchesExpectedHash(installer, 3, ABC_SHA256));
        // A failed lookup must never erase user/downloaded data.
        assertTrue(installer.isFile());
        assertTrue(installer.length() == 3);
    }

    @Test public void refusesMissingFileAndDirectory() throws Exception {
        File missing = new File(temporaryFolder.getRoot(), "not-downloaded.exe");
        File directory = temporaryFolder.newFolder("Firefox-Setup-directory.exe");

        assertFalse(FirefoxInstaller.matchesExpectedHash(null, 3, ABC_SHA256));
        assertFalse(FirefoxInstaller.matchesExpectedHash(missing, 3, ABC_SHA256));
        assertFalse(FirefoxInstaller.matchesExpectedHash(directory, 3, ABC_SHA256));
    }
}
