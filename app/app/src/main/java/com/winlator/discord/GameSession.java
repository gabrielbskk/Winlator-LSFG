package com.winlator.discord;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/** Session identity and active time; independent of Android and the SDK. */
public final class GameSession {
    private static final Set<String> SYSTEM_PROCESSES = new HashSet<>(Arrays.asList(
        "explorer.exe", "services.exe", "winedevice.exe", "wineboot.exe", "winemenubuilder.exe",
        "rpcss.exe", "svchost.exe", "conhost.exe", "start.exe", "winhandler.exe", "wfm.exe",
        "taskmgr.exe", "control.exe", "regedit.exe", "cmd.exe", "rundll32.exe", "notepad.exe",
        "steam.exe", "steamwebhelper.exe", "epicgameslauncher.exe", "epicwebhelper.exe",
        "galaxyclient.exe", "galaxyclient helper.exe", "ubisoftconnect.exe", "upc.exe",
        "eadesktop.exe", "ealauncher.exe", "eabackgroundservice.exe"));
    private int pid;
    private String executable = "";
    private String title = "";
    private long accumulatedMillis;
    private long resumedAtMillis;
    private boolean paused;

    public static String executableName(String value) {
        if (value == null) return "";
        // WM_CLASS may contain instance and class, separated by NUL.
        for (String part : value.split("\u0000")) {
            String name = part.replace('\\', '/').trim().replace("\"", "");
            name = name.substring(name.lastIndexOf('/') + 1).toLowerCase(Locale.ROOT);
            int end = name.indexOf(".exe");
            if (end >= 0) return name.substring(0, end + 4);
        }
        return "";
    }

    public static boolean isGameExecutable(String value) {
        String name = executableName(value);
        return !name.isEmpty() && !SYSTEM_PROCESSES.contains(name);
    }

    public static boolean matchesWindow(String windowClass, int windowPid, String processName, int processPid) {
        if (!isGameExecutable(processName)) return false;
        String wmExecutable = executableName(windowClass);
        return wmExecutable.isEmpty() ? windowPid == processPid
            : wmExecutable.equals(executableName(processName));
    }

    public static long applicationId(String value) {
        if (value == null || !value.matches("[0-9]{17,19}")) return 0;
        try { return Long.parseLong(value); }
        catch (NumberFormatException e) { return 0; }
    }

    public void select(int newPid, String name, String displayName, long nowMillis) {
        String normalized = executableName(name);
        if (newPid <= 0 || !isGameExecutable(normalized)) { end(); return; }
        if (pid == newPid && executable.equals(normalized)) return;
        pid = newPid;
        executable = normalized;
        title = displayName == null || displayName.trim().isEmpty()
            ? normalized.substring(0, normalized.length() - 4) : displayName.trim();
        // Discord fields are small; do not expose paths or arbitrary window titles.
        if (title.length() > 128) title = title.substring(0, 128);
        accumulatedMillis = 0;
        resumedAtMillis = nowMillis;
    }

    public void pause(long nowMillis) {
        if (paused) return;
        if (pid != 0) accumulatedMillis += Math.max(0, nowMillis - resumedAtMillis);
        paused = true;
    }

    public void resume(long nowMillis) {
        if (!paused) return;
        paused = false;
        resumedAtMillis = nowMillis;
    }

    public long startSeconds(long wallMillis, long nowMillis) {
        long elapsed = accumulatedMillis + (paused || pid == 0 ? 0 : Math.max(0, nowMillis - resumedAtMillis));
        return (wallMillis - elapsed) / 1000;
    }

    public int getPid() { return pid; }
    public String getExecutable() { return executable; }
    public String getTitle() { return title; }
    public void end() { pid = 0; executable = ""; title = ""; accumulatedMillis = 0; }
}
