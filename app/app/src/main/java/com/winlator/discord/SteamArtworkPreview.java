package com.winlator.discord;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/** Downloads only Steam-generated cover URLs, strictly size-limited and off UI thread. */
public final class SteamArtworkPreview {
    private SteamArtworkPreview() {}

    public static Bitmap fetch(String appId) {
        String image = SteamGameArtwork.coverUrl(appId);
        if (image.isEmpty()) return null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(image);
            for (int hop = 0; hop < 2; hop++) {
                String host = url.getHost().toLowerCase(java.util.Locale.ROOT);
                if (!"https".equals(url.getProtocol()) ||
                    (!host.equals("shared.steamstatic.com") &&
                     !host.equals("shared.fastly.steamstatic.com"))) return null;
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(3500);
                connection.setReadTimeout(3500);
                connection.setInstanceFollowRedirects(false);
                connection.setRequestProperty("Accept", "image/jpeg,image/png");
                int status = connection.getResponseCode();
                if (status == 301 || status == 302 || status == 307 || status == 308) {
                    String location = connection.getHeaderField("Location");
                    if (location == null) return null;
                    URL redirected = new URL(url, location);
                    connection.disconnect();
                    connection = null;
                    url = redirected;
                    continue;
                }
                if (status != HttpURLConnection.HTTP_OK ||
                    connection.getContentLength() > 2500000) return null;
                String mime = connection.getContentType();
                if (mime == null ||
                    (!mime.toLowerCase(java.util.Locale.ROOT).startsWith("image/jpeg") &&
                     !mime.toLowerCase(java.util.Locale.ROOT).startsWith("image/png"))) return null;
                try (InputStream input = connection.getInputStream();
                     ByteArrayOutputStream output = new ByteArrayOutputStream()) {
                    byte[] buffer = new byte[8192];
                    int bytes;
                    while ((bytes = input.read(buffer)) != -1) {
                        if (output.size() + bytes > 2500000) return null;
                        output.write(buffer, 0, bytes);
                    }
                    byte[] data = output.toByteArray();
                    // Reject decompression bombs even if the compressed JPEG/PNG
                    // fits within the network byte limit.
                    BitmapFactory.Options bounds = new BitmapFactory.Options();
                    bounds.inJustDecodeBounds = true;
                    BitmapFactory.decodeByteArray(data, 0, data.length, bounds);
                    if (bounds.outWidth <= 0 || bounds.outHeight <= 0 ||
                        bounds.outWidth > 8192 || bounds.outHeight > 8192) return null;
                    BitmapFactory.Options opts = new BitmapFactory.Options();
                    int sample = 1;
                    while (bounds.outWidth / sample > 1024 ||
                           bounds.outHeight / sample > 1024) sample *= 2;
                    opts.inSampleSize = sample;
                    return BitmapFactory.decodeByteArray(data, 0, data.length, opts);
                }
            }
        } catch (IOException | RuntimeException ignored) {
            // If Steam CDN is inaccessible, show the local EXE icon instead.
        } finally {
            if (connection != null) connection.disconnect();
        }
        return null;
    }
}
