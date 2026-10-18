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

    @Test public void prefersAlreadyInstalledAuroraVesktopWithoutDownloading() throws Exception {
        File downloads = temporaryFolder.newFolder("Downloads");
        File folder = new File(downloads, "Vesktop-1.6.7-win");
        folder.mkdirs();
        File downloaded = new File(folder, "vesktop.exe");
        downloaded.createNewFile();
        File installed = temporaryFolder.newFile("vesktop-installed.exe");

        assertEquals(installed, WineStartMenuCreator.chooseVesktopExecutable(installed, downloads));
    }

    @Test public void fallsBackToExistingDownloadsVesktop() throws Exception {
        File downloads = temporaryFolder.newFolder("Downloads-v2");
        File folder = new File(downloads, "Vesktop-1.6.7-win");
        folder.mkdirs();
        File downloaded = new File(folder, "vesktop.exe");
        downloaded.createNewFile();

        assertEquals(downloaded, WineStartMenuCreator.chooseVesktopExecutable(null, downloads));
        assertEquals(downloaded, WineStartMenuCreator.chooseVesktopExecutable(
            new File(temporaryFolder.getRoot(), "not-installed.exe"), downloads));
    }

    @Test public void ignoresZipWithoutExtractedExecutable() throws Exception {
        temporaryFolder.newFile("Vesktop-1.6.7-win.zip");

        assertNull(WineStartMenuCreator.findDownloadedVesktop(temporaryFolder.getRoot()));
    }

    @Test public void findsEndcordGuiInExtractedDownloadFolder() throws Exception {
        File folder = temporaryFolder.newFolder("Endcord-1.5.4-win");
        File executable = new File(folder, "endcord-gui.exe");
        executable.createNewFile();
        assertEquals(executable, WineStartMenuCreator.findDownloadedEndcord(temporaryFolder.getRoot()));
    }

    @Test public void ignoresEndcordArchiveWithoutExtractedExecutable() throws Exception {
        temporaryFolder.newFile("Endcord-1.5.4-win.zip");
        assertNull(WineStartMenuCreator.findDownloadedEndcord(temporaryFolder.getRoot()));
    }

    @Test public void findsLegcordInExtractedWindowsZip() throws Exception {
        File folder = temporaryFolder.newFolder("Legcord-1.3.1-win-x64");
        File executable = new File(folder, "Legcord.exe");
        executable.createNewFile();
        assertEquals(executable, WineStartMenuCreator.findDownloadedLegcord(temporaryFolder.getRoot()));
    }

    @Test public void ignoresLegcordInstallerWithoutExtractedExecutable() throws Exception {
        temporaryFolder.newFile("Legcord-1.3.1-win-x64.exe");
        assertNull(WineStartMenuCreator.findDownloadedLegcord(temporaryFolder.getRoot()));
    }

    @Test public void findsBrowserInstalledInContainerPrefix() throws Exception {
        File container = temporaryFolder.newFolder("container");
        File executable = new File(container,
            ".wine/drive_c/Program Files/Google/Chrome/Application/chrome.exe");
        executable.getParentFile().mkdirs(); executable.createNewFile();
        assertEquals(executable, WineStartMenuCreator.findInstalledBrowser(container,
            "Program Files/Google/Chrome/Application/chrome.exe"));
        assertNull(WineStartMenuCreator.findInstalledBrowser(container,
            "Program Files/Mozilla Firefox/firefox.exe"));
    }

    @Test public void usesConfiguredDownloadDriveForVesktopShortcut() throws Exception {
        File downloads = temporaryFolder.newFolder("Download");
        File folder = new File(downloads, "Vesktop-1.6.7-win");
        folder.mkdirs();
        File executable = new File(folder, "vesktop.exe");
        executable.createNewFile();
        assertEquals("D:/Vesktop-1.6.7-win/vesktop.exe",
            WineStartMenuCreator.toWindowsPath("D:" + downloads.getAbsolutePath(), executable));
    }
}
