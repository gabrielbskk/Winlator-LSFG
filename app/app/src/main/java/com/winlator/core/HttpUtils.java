package com.winlator.core;

import android.app.Activity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class HttpUtils {
    // Bounded, shared workers avoid leaking one executor per download.
    private static final java.util.concurrent.ExecutorService DOWNLOAD_WORKERS = Executors.newFixedThreadPool(2);

    private static void downloadAsync(String url, Callback<String> onDownloadComplete) {
        HttpURLConnection connection = null;
        String result = null;
        try {
            connection = (HttpURLConnection)(new URL(url)).openConnection();
            connection.setConnectTimeout(20000);
            connection.setReadTimeout(45000);
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (InputStream inStream = connection.getInputStream()) {
                    byte[] bytes = StreamUtils.copyToByteArray(inStream);
                    result = new String(bytes, StandardCharsets.UTF_8);
                }
            }
        }
        catch (Exception error) {
            android.util.Log.w("HttpUtils", "Text download failed", error);
        }
        finally {
            if (connection != null) connection.disconnect();
            onDownloadComplete.call(result);
        }
    }

    public static void download(final String url, final Callback<String> onDownloadComplete) {
        DOWNLOAD_WORKERS.execute(() -> downloadAsync(url, onDownloadComplete));
    }

    private static void downloadAsync(String url, File destination, AtomicBoolean interruptRef,
            Callback<Integer> onPublishProgress, Callback<Boolean> onDownloadComplete) {
        HttpURLConnection connection = null;
        boolean success = false;
        try {
            if (!interruptRef.get()) {
                connection = (HttpURLConnection)(new URL(url)).openConnection();
                connection.setConnectTimeout(20000);
                connection.setReadTimeout(45000);
                connection.setRequestProperty("Accept-Encoding", "identity");
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    try (InputStream inStream = new BufferedInputStream(
                            connection.getInputStream(), StreamUtils.BUFFER_SIZE)) {
                        success = AtomicDownload.write(inStream, destination,
                                connection.getContentLengthLong(), interruptRef, onPublishProgress);
                    }
                }
            }
        }
        catch (Exception error) {
            android.util.Log.w("HttpUtils", "Component download failed", error);
        }
        finally {
            if (connection != null) connection.disconnect();
            onDownloadComplete.call(success);
        }
    }

    public static void download(final Activity activity, final String url, final File destination, final Callback<Boolean> onDownloadComplete) {
        final DownloadProgressDialog dialog = new DownloadProgressDialog(activity);
        final AtomicBoolean interruptRef = new AtomicBoolean();
        dialog.show(() -> interruptRef.set(true));
        DOWNLOAD_WORKERS.execute(() -> {
            downloadAsync(url, destination, interruptRef, (progress) -> {
                activity.runOnUiThread(() -> {
                    dialog.setProgress(progress);
                });
            }, (success) -> {
                activity.runOnUiThread(() -> {
                    dialog.close();
                    onDownloadComplete.call(success);
                });
            });
        });
    }
}
