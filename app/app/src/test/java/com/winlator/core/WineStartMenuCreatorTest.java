package com.winlator.core;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class WineStartMenuCreatorTest {
    @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test public void findsExtractedVesktopInVersionedDownloadFolder() throws Exception {
        File folder = temporaryFolder.newFolder("Vesktop-1.6.7-win");
        File executable = new File(folder, "vesktop.exe");
        executable.createNewFile();

        assertEquals(executable, WineStartMenuCreator.findDownloadedVesktop(temporaryFolder.getRoot()));
    }

    @Test public void ignoresZipWithoutExtractedExecutable() throws Exception {
        temporaryFolder.newFile("Vesktop-1.6.7-win.zip");

        assertNull(WineStartMenuCreator.findDownloadedVesktop(temporaryFolder.getRoot()));
    }
}
