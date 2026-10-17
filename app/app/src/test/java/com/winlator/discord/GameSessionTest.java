package com.winlator.discord;

import org.junit.Test;
import static org.junit.Assert.*;

public class GameSessionTest {
    @Test public void matchesWineClassWhenUnixAndWindowsPidsDiffer() {
        assertTrue(GameSession.matchesWindow("game.exe\u0000Wine", 123, "Game.EXE", 900));
        assertFalse(GameSession.matchesWindow("other.exe", 900, "Game.EXE", 900));
        assertTrue(GameSession.matchesWindow("Wine", 900, "Game.EXE", 900));
        assertFalse(GameSession.matchesWindow("Wine", 123, "Game.EXE", 900));
    }

    @Test public void ignoresWineDesktopAndKnownLaunchers() {
        for (String name : new String[]{"explorer.exe", "wineboot.exe", "winhandler.exe", "wfm.exe",
            "steam.exe", "steamwebhelper.exe", "EpicGamesLauncher.exe", "EADesktop.exe", "cmd.exe"}) {
            assertFalse(name, GameSession.isGameExecutable(name));
            assertFalse(GameSession.matchesWindow(name, 10, name, 10));
        }
        assertFalse(GameSession.isGameExecutable(""));
        assertFalse(GameSession.isGameExecutable(null));
    }

    @Test public void parsesExecutableWithoutPublishingPathsOrArguments() {
        assertEquals("game.exe", GameSession.executableName("\"C:\\Games\\Example\\Game.EXE\" -fullscreen"));
        assertEquals("game.exe", GameSession.executableName("Wine\u0000game.exe\u0000"));
        GameSession session = new GameSession();
        session.select(10, "C:\\Games\\game.exe", null, 1000);
        assertEquals("game", session.getTitle());
    }

    @Test public void stableSessionSurvivesWindowRecreationAndTitleChanges() {
        GameSession session = new GameSession();
        session.select(10, "game.exe", "My game", 1000);
        session.select(10, "GAME.EXE", "Different window", 11000);
        assertEquals("My game", session.getTitle());
        assertEquals(100, session.startSeconds(120000, 21000));
    }

    @Test public void restartOfSameExecutableCreatesNewSession() {
        GameSession session = new GameSession();
        session.select(10, "game.exe", "Game", 1000);
        session.select(20, "game.exe", "Game", 21000);
        assertEquals(120, session.startSeconds(120000, 21000));
    }

    @Test public void suspensionIsExcludedAndDuplicatePauseIsHarmless() {
        GameSession session = new GameSession();
        session.select(10, "game.exe", null, 1000);
        session.pause(11000);
        session.pause(15000);
        assertEquals(140, session.startSeconds(150000, 51000));
        session.resume(51000);
        session.resume(55000);
        assertEquals(140, session.startSeconds(160000, 61000));
    }

    @Test public void selectionWhileSuspendedDoesNotAccumulateTime() {
        GameSession session = new GameSession();
        session.pause(1000);
        session.select(10, "game.exe", null, 2000);
        session.resume(10000);
        assertEquals(100, session.startSeconds(105000, 15000));
    }

    @Test public void endingGameDropsIdentityAndElapsedTime() {
        GameSession session = new GameSession();
        session.select(10, "game.exe", null, 1000);
        session.end();
        assertEquals(0, session.getPid());
        assertEquals("", session.getTitle());
        session.select(11, "other.exe", null, 20000);
        assertEquals(100, session.startSeconds(100000, 20000));
    }

    @Test public void invalidSelectionCannotPublishSystemProcess() {
        GameSession session = new GameSession();
        session.select(10, "game.exe", null, 1000);
        session.select(11, "explorer.exe", "Desktop", 10000);
        assertEquals(0, session.getPid());
    }

    @Test public void validatesPublicIdAndRejectsTokensAndOverflow() {
        assertEquals(123456789012345678L, GameSession.applicationId("123456789012345678"));
        for (String input : new String[]{"", "123", "token.secret", "-123456789012345678", "9999999999999999999", "12345678901234567890"})
            assertEquals(input, 0, GameSession.applicationId(input));
    }
}
