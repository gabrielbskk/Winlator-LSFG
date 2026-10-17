package com.winlator.discord;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/** User-initiated search; never silently assigns a match based on the EXE name. */
public final class SteamStoreSearch {
    private SteamStoreSearch() {}

    public static final class Match {
        public final String id;
        public final String title;
        Match(String id, String title) { this.id = id; this.title = title; }
        @Override public String toString() { return title + "  (Steam " + id + ")"; }
    }

    public static List<Match> search(String query) throws Exception {
        String term = query == null ? "" : query.trim();
        if (term.length() < 2 || term.length() > 80)
            throw new IllegalArgumentException("Search name must be 2-80 characters");
        // This endpoint is operated by Steam and can change without notice.
        URL url = new URL("https://store.steampowered.com/api/storesearch/?term=" +
            URLEncoder.encode(term, "UTF-8") + "&l=english&cc=US");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(4500);
        connection.setReadTimeout(4500);
        connection.setInstanceFollowRedirects(false);
        connection.setRequestProperty("Accept", "application/json");
        try {
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
                throw new java.io.IOException("Steam search unavailable");
            try (InputStream input = connection.getInputStream();
                 ByteArrayOutputStream output = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[4096];
                int read;
                while ((read = input.read(buffer)) >= 0) {
                    if (output.size() + read > 131072)
                        throw new java.io.IOException("Steam response too large");
                    output.write(buffer, 0, read);
                }
                JSONObject result = new JSONObject(output.toString("UTF-8"));
                JSONArray items = result.optJSONArray("items");
                List<Match> matches = new ArrayList<>();
                if (items == null) return matches;
                for (int i = 0; i < Math.min(10, items.length()); i++) {
                    JSONObject item = items.optJSONObject(i);
                    if (item == null) continue;
                    String id = SteamGameArtwork.appId(item.optString("id"));
                    String name = item.optString("name", "").trim();
                    if (!id.isEmpty() && !name.isEmpty() && name.length() <= 128)
                        matches.add(new Match(id, name));
                }
                return matches;
            }
        } finally {
            connection.disconnect();
        }
    }
}
