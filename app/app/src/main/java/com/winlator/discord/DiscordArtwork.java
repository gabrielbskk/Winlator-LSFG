package com.winlator.discord;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

/**
 * Rich Presence images are Discord Developer Portal asset keys or public HTTPS
 * URLs. A local EXE/ICO/PNG path cannot be displayed by Discord.
 *
 * Never upload local icons or executable content automatically.
 */
public final class DiscordArtwork {
    private DiscordArtwork() {}

    /** An empty result means "use Discord's default application icon". */
    public static String normalize(String value) {
        if (value == null) return "";
        String trimmed = value.trim();
        if (trimmed.isEmpty() || trimmed.length() > 300) return "";

        // Portal image keys are short names; paths, tokens and local URLs are not.
        if (trimmed.matches("[A-Za-z0-9][A-Za-z0-9_-]{0,299}")) {
            return trimmed.toLowerCase(Locale.ROOT);
        }

        try {
            URI uri = new URI(trimmed);
            if (!"https".equalsIgnoreCase(uri.getScheme()) || uri.getHost() == null ||
                uri.getRawUserInfo() != null || uri.getFragment() != null) return "";
            String host = uri.getHost().toLowerCase(Locale.ROOT);
            if (!host.contains(".") || host.equals("localhost") ||
                host.endsWith(".localhost") || host.endsWith(".local") ||
                host.endsWith(".internal") || host.endsWith(".test")) return "";
            // Avoid nonstandard local endpoints and malformed addresses.
            if (uri.getPort() != -1) return "";
            if (trimmed.indexOf('\n') != -1 || trimmed.indexOf('\r') != -1) return "";
            return trimmed;
        } catch (URISyntaxException error) {
            return "";
        }
    }
}
