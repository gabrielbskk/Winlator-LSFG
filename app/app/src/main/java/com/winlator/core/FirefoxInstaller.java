package com.winlator.core;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/** Downloads the official Firefox ESR Windows installer for execution inside Wine. */
public final class FirefoxInstaller {
    public static final String VERSION = "153.0esr";
    private static final String URL = "https://ftp.mozilla.org/pub/firefox/releases/153.0esr/win64/en-US/Firefox%20Setup%20153.0esr.exe";
    private static final long SIZE = 89693456L;
    private static final String SHA256 = "d3bcf6b8b8bdf769b9279980402812799642c27ba2fb2b7dcfe04a0ac972f5a0";

    private FirefoxInstaller() {}

    /**
     * Recheck cached installers before exposing a Wine shortcut. A matching
     * filename and byte count do not prove that a download is authentic.
     * Never delete an existing file on a failed check.
     */
    static boolean matchesExpectedHash(File file, long expectedSize, String expectedSha256) {
        if (file == null || !file.isFile() || file.length() != expectedSize) return false;
        try {
            return DiscordClientInstaller.sha256(file).equalsIgnoreCase(expectedSha256);
        }
        catch (IOException | NoSuchAlgorithmException error) {
            Log.w("FirefoxInstaller", "Cached Firefox installer could not be verified", error);
            return false;
        }
    }

    public static File getInstaller(Context context) {
        File external = context.getExternalFilesDir(null);
        File root = new File(external != null ? external : context.getFilesDir(), "browser-installers");
        File installer = new File(root, "Firefox-Setup-" + VERSION + "-win64.exe");
        return matchesExpectedHash(installer, SIZE, SHA256) ? installer : null;
    }

    public static void download(Activity activity, Callback<Boolean> callback) {
        if (getInstaller(activity) != null) {
            callback.call(true);
            return;
        }
        File external = activity.getExternalFilesDir(null);
        File root = new File(external != null ? external : activity.getFilesDir(), "browser-installers");
        if ((!root.isDirectory() && !root.mkdirs()) || root.getUsableSpace() < SIZE + 64_000_000L) {
            callback.call(false);
            return;
        }
        File installer = new File(root, "Firefox-Setup-" + VERSION + "-win64.exe");
        HttpUtils.download(activity, URL, installer, downloaded -> {
            boolean valid = false;
            if (downloaded) {
                try {
                    valid = matchesExpectedHash(installer, SIZE, SHA256);
                }
                catch (Exception error) {
                    Log.w("FirefoxInstaller", "Verification failed", error);
                }
            }
            if (!valid) installer.delete();
            callback.call(valid);
        });
    }
}
