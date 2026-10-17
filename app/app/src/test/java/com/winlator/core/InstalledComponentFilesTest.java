package com.winlator.core;

import org.junit.Test;
import static org.junit.Assert.*;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

/** Confirm interrupted downloads cannot appear as installed driver versions. */
public class InstalledComponentFilesTest {
    @Test public void validTurnipAndOtherComponentsAreListed() throws Exception {
        Path root = Files.createTempDirectory("aurora-installed-");
        try {
            File turnip = Files.write(root.resolve("turnip-26.1.0.tzst"),
                    "driver".getBytes(StandardCharsets.UTF_8)).toFile();
            File dxvk = Files.write(root.resolve("dxvk-2.4.1.tzst"),
                    "dxvk".getBytes(StandardCharsets.UTF_8)).toFile();
            assertTrue(InstalledComponentFiles.isSelectable("turnip", turnip));
            assertTrue(InstalledComponentFiles.isSelectable("dxvk", dxvk));
            assertFalse(InstalledComponentFiles.isSelectable("turnip", dxvk));
        } finally { delete(root); }
    }

    @Test public void partialDownloadsAndWrongFilesAreNotSelectable() throws Exception {
        Path root = Files.createTempDirectory("aurora-installed-");
        try {
            for (String filename : new String[]{
                    ".aurora-download-123.partial",
                    "turnip-26.1.0.tzst.partial",
                    "turnip-.tzst",
                    "turnip-25.0.0.zip",
                    "README.txt"}) {
                File entry = Files.write(root.resolve(filename), new byte[]{1}).toFile();
                assertFalse(filename, InstalledComponentFiles.isSelectable("turnip", entry));
            }
            assertFalse(InstalledComponentFiles.isSelectable("turnip", root.resolve("turnip-26.1.0.tzst").toFile()));
        } finally { delete(root); }
    }

    @Test public void otherComponentFormatsRemainCompatible() throws Exception {
        Path root = Files.createTempDirectory("aurora-installed-");
        try {
            File customDriver = Files.createDirectory(root.resolve("My driver")).toFile();
            File soundfont = Files.write(root.resolve("My Bank.sf2"), new byte[]{1}).toFile();
            File invalid = Files.write(root.resolve("soundfont-.partial"), new byte[]{1}).toFile();
            assertTrue(InstalledComponentFiles.isSelectable("adrenotools_driver", customDriver));
            assertTrue(InstalledComponentFiles.isSelectable("soundfont", soundfont));
            File upperExtension = Files.write(root.resolve("Legacy.SF2"), new byte[]{1}).toFile();
            assertFalse(InstalledComponentFiles.isSelectable("soundfont", upperExtension));
            assertFalse(InstalledComponentFiles.isSelectable("adrenotools_driver", soundfont));
            assertFalse(InstalledComponentFiles.isSelectable("soundfont", invalid));
            assertFalse(InstalledComponentFiles.isSelectable(null, soundfont));
            assertFalse(InstalledComponentFiles.isSelectable("turnip", null));
        } finally { delete(root); }
    }

    private static void delete(Path root) throws Exception {
        try (java.util.stream.Stream<Path> paths = Files.walk(root)) {
            paths.sorted(Comparator.reverseOrder()).forEach(path -> path.toFile().delete());
        }
    }
}
