package com.winlator.discord;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class DiscordArtworkTest {
    @Test public void acceptsDeveloperPortalAssetKeys() {
        assertEquals("garrys-mod", DiscordArtwork.normalize("Garrys-Mod"));
        assertEquals("game_2026", DiscordArtwork.normalize(" game_2026 "));
    }

    @Test public void acceptsPublicHttpsArtworkUrls() {
        assertEquals("https://example.org/game.png",
            DiscordArtwork.normalize("https://example.org/game.png"));
    }

    @Test public void rejectsLocalIconsAndNonHttpsUrls() {
        for (String bad : new String[] {
            "file:///storage/game.png", "content://app/game.png",
            "C:\\Games\\game.exe", "/sdcard/icon.png", "data:image/png;base64,AAAA",
            "http://example.org/game.png", "https://localhost/game.png",
            "https://example.local/game.png", "https://foo:8080/game.png",
            "https://user:password@example.com/x.png", "https://example.org/img.png#secret",
            "icon png"
        }) assertEquals(bad, "", DiscordArtwork.normalize(bad));
    }

    @Test public void safelyFallsBackWhenBlankOrTooLong() {
        assertEquals("", DiscordArtwork.normalize(null));
        assertEquals("", DiscordArtwork.normalize(" "));
        assertEquals("", DiscordArtwork.normalize("a".repeat(301)));
    }
}
