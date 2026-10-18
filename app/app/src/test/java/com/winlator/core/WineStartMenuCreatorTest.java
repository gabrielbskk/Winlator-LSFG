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
        java.nio.file.Files.write(executable.toPath(), new byte[]{0x4d, 0x5a});

        assertEquals(executable, WineStartMenuCreator.findDownloadedVesktop(temporaryFolder.getRoot()));
    }

    @Test public void prefersAlreadyInstalledAuroraVesktopWithoutDownloading() throws Exception {
        File downloads = temporaryFolder.newFolder("Downloads");
        File folder = new File(downloads, "Vesktop-1.6.7-win");
        folder.mkdirs();
        File downloaded = new File(folder, "vesktop.exe");
        java.nio.file.Files.write(downloaded.toPath(), new byte[]{0x4d, 0x5a});
        File installed = temporaryFolder.newFile("vesktop-installed.exe");
        java.nio.file.Files.write(installed.toPath(), new byte[]{0x4d, 0x5a});

        assertEquals(installed, WineStartMenuCreator.chooseVesktopExecutable(installed, downloads));
    }

    @Test public void fallsBackToExistingDownloadsVesktop() throws Exception {
        File downloads = temporaryFolder.newFolder("Downloads-v2");
        File folder = new File(downloads, "Vesktop-1.6.7-win");
        folder.mkdirs();
        File downloaded = new File(folder, "vesktop.exe");
        java.nio.file.Files.write(downloaded.toPath(), new byte[]{0x4d, 0x5a});

        assertEquals(downloaded, WineStartMenuCreator.chooseVesktopExecutable(null, downloads));
        assertEquals(downloaded, WineStartMenuCreator.chooseVesktopExecutable(
            new File(temporaryFolder.getRoot(), "not-installed.exe"), downloads));
    }

    @Test public void fallsBackToDownloadsWhenAuroraExecutableIsEmpty() throws Exception {
        File downloads = temporaryFolder.newFolder("working-vesktop");
        File dir = new File(downloads, "Vesktop-1.6.7-win");
        dir.mkdirs();
        File downloaded = new File(dir, "vesktop.exe");
        java.nio.file.Files.write(downloaded.toPath(), new byte[]{0x4d, 0x5a});
        File invalidInstalled = temporaryFolder.newFile("empty-aurora-vesktop.exe");

        assertEquals(downloaded, WineStartMenuCreator.chooseVesktopExecutable(invalidInstalled, downloads));
    }

    @Test public void skipsEmptyVesktopDownloadInsteadOfCreatingDeadShortcut() throws Exception {
        File downloads = temporaryFolder.newFolder("empty-vesktop");
        File folder = new File(downloads, "Vesktop-1.6.7-win");
        folder.mkdirs();
        new File(folder, "vesktop.exe").createNewFile();

        assertNull(WineStartMenuCreator.findDownloadedVesktop(downloads));
        assertNull(WineStartMenuCreator.chooseVesktopExecutable(null, downloads));
    }

    @Test public void ignoresEmptyLegacyClientsAndBrowserExecutables() throws Exception {
        File downloads = temporaryFolder.newFolder("empty-other-clients");
        File endcord = new File(downloads, "Endcord-1.5.4-win");
        File legcord = new File(downloads, "Legcord-1.3.1-win-x64");
        endcord.mkdirs();
        legcord.mkdirs();
        new File(endcord, "endcord-gui.exe").createNewFile();
        new File(legcord, "Legcord.exe").createNewFile();

        assertNull(WineStartMenuCreator.findDownloadedEndcord(downloads));
        assertNull(WineStartMenuCreator.findDownloadedLegcord(downloads));

        File wineRoot = temporaryFolder.newFolder("empty-wine");
        File chrome = new File(wineRoot,
            ".wine/drive_c/Program Files/Google/Chrome/Application/chrome.exe");
        chrome.getParentFile().mkdirs();
        chrome.createNewFile();
        assertNull(WineStartMenuCreator.findInstalledBrowser(wineRoot,
            "Program Files/Google/Chrome/Application/chrome.exe"));
    }

    @Test public void ignoresZipWithoutExtractedExecutable() throws Exception {
        temporaryFolder.newFile("Vesktop-1.6.7-win.zip");

        assertNull(WineStartMenuCreator.findDownloadedVesktop(temporaryFolder.getRoot()));
    }

    @Test public void findsEndcordGuiInExtractedDownloadFolder() throws Exception {
        File folder = temporaryFolder.newFolder("Endcord-1.5.4-win");
        File executable = new File(folder, "endcord-gui.exe");
        java.nio.file.Files.write(executable.toPath(), new byte[]{0x4d, 0x5a});
        assertEquals(executable, WineStartMenuCreator.findDownloadedEndcord(temporaryFolder.getRoot()));
    }

    @Test public void ignoresEndcordArchiveWithoutExtractedExecutable() throws Exception {
        temporaryFolder.newFile("Endcord-1.5.4-win.zip");
        assertNull(WineStartMenuCreator.findDownloadedEndcord(temporaryFolder.getRoot()));
    }

    @Test public void findsLegcordInExtractedWindowsZip() throws Exception {
        File folder = temporaryFolder.newFolder("Legcord-1.3.1-win-x64");
        File executable = new File(folder, "Legcord.exe");
        java.nio.file.Files.write(executable.toPath(), new byte[]{0x4d, 0x5a});
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
        executable.getParentFile().mkdirs(); java.nio.file.Files.write(executable.toPath(), new byte[]{0x4d, 0x5a});
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
        java.nio.file.Files.write(executable.toPath(), new byte[]{0x4d, 0x5a});
        assertEquals("D:/Vesktop-1.6.7-win/vesktop.exe",
            WineStartMenuCreator.toWindowsPath("D:" + downloads.getAbsolutePath(), executable));
    }
}
