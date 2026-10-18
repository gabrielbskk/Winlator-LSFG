package com.winlator.core;

import com.winlator.container.Container;

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

    @Test public void usesConfiguredDownloadDriveForVesktopShortcut() throws Exception {
        File downloads = temporaryFolder.newFolder("Download");
        File folder = new File(downloads, "Vesktop-1.6.7-win");
        folder.mkdirs();
        File executable = new File(folder, "vesktop.exe");
        executable.createNewFile();
        Container container = new Container(1);
        container.setDrives("D:" + downloads.getAbsolutePath());

        assertEquals("D:/Vesktop-1.6.7-win/vesktop.exe",
            WineStartMenuCreator.toWindowsPath(container, executable));
    }
}
