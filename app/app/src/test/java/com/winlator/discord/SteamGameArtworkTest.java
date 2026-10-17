package com.winlator.discord;

import org.junit.Test;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import static org.junit.Assert.assertEquals;

public class SteamGameArtworkTest {
    @Test public void acceptsSteamAndSteamDbIdentifiers() {
        assertEquals("620", SteamGameArtwork.appId("620"));
        assertEquals("620", SteamGameArtwork.appId("https://steamdb.info/app/620/"));
        assertEquals("620", SteamGameArtwork.appId("https://store.steampowered.com/app/620/Portal_2/"));
    }

    @Test public void rejectsOtherUrlsAndBadIds() {
        for (String bad : new String[] {"", "0", "-62", "not-steam", "99999999999",
            "https://example.com/app/620", "http://steamdb.info/app/620/",
            "https://steamdb.info.evil.example/app/620",
            "https://steamdb.info/app/620/?token=hello",
            "https://steamdb.info/app/620#more"}) assertEquals(bad, "", SteamGameArtwork.appId(bad));
    }

    @Test public void constructsCoverFromValidatedId() {
        assertEquals("https://shared.steamstatic.com/store_item_assets/steam/apps/620/library_600x900.jpg",
            SteamGameArtwork.coverUrl("620"));
        assertEquals("", SteamGameArtwork.coverUrl("invalid"));
    }

    @Test public void readsOnlyLocalSteamMarker() throws Exception {
        File dir = Files.createTempDirectory("steam-rpc").toFile();
        File exe = new File(dir, "game.exe");
        File marker = new File(dir, "steam_appid.txt");
        try {
            if (!exe.createNewFile()) throw new Exception("Test file exists");
            try (FileOutputStream out = new FileOutputStream(marker)) {
                out.write("  620  \n".getBytes(StandardCharsets.UTF_8));
            }
            assertEquals("620", SteamGameArtwork.localId(exe));
            assertEquals("", SteamGameArtwork.localId(new File(dir, "not-a-game.txt")));
        } finally {
            marker.delete();
            exe.delete();
            dir.delete();
        }
    }
}
