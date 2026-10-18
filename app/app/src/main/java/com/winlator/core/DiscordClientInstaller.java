package com.winlator.core;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.winlator.R;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Optional, pinned Windows Discord clients. No client binary is shipped in the APK. */
public final class DiscordClientInstaller {
    public enum Client {
        ENDCORD("Endcord", "1.5.4", "endcord-gui.exe", 58169343L, 58674187L,
            "https://github.com/sparklost/endcord/releases/download/1.5.4/endcord-gui-1.5.4-windows.zip",
            "7c1ef34c18262ff87072de38afadc54d0c1daedf391d0c2b872cfce52db4d12f"),
        LEGCORD("LegCord", "1.3.1", "Legcord.exe", 183800269L, 500052174L,
            "https://github.com/Legcord/Legcord/releases/download/v1.3.1/Legcord-1.3.1-win-x64.zip",
            "36937b53c9eb898041d790a48faf0d6058f8dbdee6b34dc844bf67c69cc14c2e"),
        VESKTOP("Vesktop", "1.6.7", "vesktop.exe", 144162512L, 398103488L,
            "https://github.com/Vencord/Vesktop/releases/download/v1.6.7/Vesktop-1.6.7-win.zip",
            "ff733d40835a603239a43ec79d53131749dcebd8611b72d301f30cfb9b1fbfa3");

        public final String name;
        public final String version;
        public final String executable;
        public final long archiveBytes;
        public final long extractedBytes;
        public final String url;
        public final String sha256;

        Client(String name, String version, String executable, long archiveBytes, long extractedBytes, String url, String sha256) {
            this.name = name;
            this.version = version;
            this.executable = executable;
            this.archiveBytes = archiveBytes;
            this.extractedBytes = extractedBytes;
            this.url = url;
            this.sha256 = sha256;
        }

        String directoryName() { return name.toLowerCase() + "-" + version; }
    }

    private static final ExecutorService INSTALL_WORKER = Executors.newSingleThreadExecutor();
    private static final long MAX_EXTRACTED_BYTES = 1_500_000_000L;
    private DiscordClientInstaller() {}

    private static File installRoot(Context context) {
        File external = context.getExternalFilesDir(null);
        return new File(external != null ? external : context.getFilesDir(), "discord-clients");
    }

    public static File getInstalledExecutable(Context context, Client client) {
        File executable = new File(new File(installRoot(context), client.directoryName()), client.executable);
        return executable.isFile() ? executable : null;
    }

    public static void install(Activity activity, Client client, Callback<Boolean> callback) {
        File root = installRoot(activity);
        if (getInstalledExecutable(activity, client) != null) {
            callback.call(true);
            return;
        }
        // Allow room for the archive, extracted files and an install staging directory.
        if ((!root.isDirectory() && !root.mkdirs()) || root.getUsableSpace() < client.archiveBytes + client.extractedBytes + 64_000_000L) {
            callback.call(false);
            return;
        }
        File archive = new File(root, "." + client.directoryName() + ".zip");
        HttpUtils.download(activity, client.url, archive, downloaded -> {
            if (!downloaded) {
                callback.call(false);
                return;
            }
            PreloaderDialog preloader = new PreloaderDialog(activity);
            preloader.show(R.string.discord_clients_installing);
            INSTALL_WORKER.execute(() -> {
                boolean installed = installArchive(archive, root, client);
                archive.delete();
                activity.runOnUiThread(() -> {
                    preloader.close();
                    callback.call(installed);
                });
            });
        });
    }

    static boolean installArchive(File archive, File root, Client client) {
        File stage = null;
        try {
            if (archive.length() != client.archiveBytes || !sha256(archive).equals(client.sha256) ||
                !archiveFits(archive)) return false;
            File target = new File(root, client.directoryName());
            if (new File(target, client.executable).isFile()) return true;
            stage = File.createTempFile(".aurora-discord-", ".install", root);
            if (!stage.delete() || !stage.mkdirs() || !ZipUtils.extract(archive, stage)) return false;
            if (!new File(stage, client.executable).isFile()) return false;
            Files.move(stage.toPath(), target.toPath(), StandardCopyOption.ATOMIC_MOVE);
            stage = null;
            return true;
        }
        catch (Exception error) {
            Log.w("DiscordClientInstaller", "Client installation failed", error);
            return false;
        }
        finally {
            if (stage != null) FileUtils.delete(stage);
        }
    }

    private static boolean archiveFits(File archive) throws IOException {
        long total = 0;
        int count = 0;
        try (ZipFile zip = new ZipFile(archive)) {
            Enumeration<ZipArchiveEntry> entries = zip.getEntries();
            while (entries.hasMoreElements()) {
                ZipArchiveEntry entry = entries.nextElement();
                if (++count > 10000 || entry.getSize() < 0 || entry.isUnixSymlink()) return false;
                total += entry.getSize();
                if (total > MAX_EXTRACTED_BYTES) return false;
            }
        }
        return count > 0;
    }

    static String sha256(File file) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (FileInputStream input = new FileInputStream(file)) {
            byte[] buffer = new byte[32768];
            int size;
            while ((size = input.read(buffer)) != -1) digest.update(buffer, 0, size);
        }
        StringBuilder hex = new StringBuilder(64);
        for (byte value : digest.digest()) hex.append(String.format(java.util.Locale.ROOT, "%02x", value & 0xff));
        return hex.toString();
    }
}
