package com.winlator.discord;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

/** Steam IDs are metadata only; no executable or personal files are uploaded. */
public final class SteamGameArtwork {
    private SteamGameArtwork() {}

    /** Accept an AppID or an official Steam/SteamDB URL, not arbitrary links. */
    public static String appId(String input) {
        if (input == null) return "";
        String text = input.trim();
        if (text.matches("[1-9][0-9]{0,9}")) return checkedId(text);
        try {
            URI uri = new URI(text);
            if (!"https".equalsIgnoreCase(uri.getScheme()) ||
                uri.getHost() == null || uri.getRawUserInfo() != null ||
                uri.getPort() != -1 || uri.getRawQuery() != null ||
                uri.getFragment() != null) return "";
            String host = uri.getHost().toLowerCase(java.util.Locale.ROOT);
            if (!host.equals("steamdb.info") && !host.equals("www.steamdb.info") &&
                !host.equals("store.steampowered.com")) return "";
            String path = uri.getPath();
            if (path == null || !path.matches("/app/[1-9][0-9]{0,9}(/[^?#]*)?")) return "";
            return checkedId(path.split("/")[2]);
        } catch (URISyntaxException ex) {
            return "";
        }
    }

    private static String checkedId(String value) {
        try {
            int id = Integer.parseInt(value);
            return id > 0 ? String.valueOf(id) : "";
        } catch (NumberFormatException ex) {
            return "";
        }
    }

    /** Steam-hosted art. Some games have no library cover at this URL. */
    public static String coverUrl(String input) {
        String id = appId(input);
        return id.isEmpty() ? "" :
            "https://shared.steamstatic.com/store_item_assets/steam/apps/" + id + "/library_600x900.jpg";
    }

    /** Only examine steam_appid.txt next to the actual executable. */
    public static String localId(File executable) {
        if (executable == null || !executable.isFile() ||
            !executable.getName().toLowerCase(java.util.Locale.ROOT).endsWith(".exe"))
            return "";
        File folder = executable.getParentFile();
        if (folder == null) return "";
        File marker = new File(folder, "steam_appid.txt");
        if (!marker.isFile() || marker.length() == 0 || marker.length() > 64) return "";
        byte[] bytes = new byte[(int) marker.length()];
        try (FileInputStream stream = new FileInputStream(marker)) {
            int offset = 0;
            while (offset < bytes.length) {
                int n = stream.read(bytes, offset, bytes.length - offset);
                if (n < 0) return "";
                offset += n;
            }
            return appId(new String(bytes, StandardCharsets.UTF_8).trim());
        } catch (IOException | SecurityException ignored) {
            return "";
        }
    }
}
