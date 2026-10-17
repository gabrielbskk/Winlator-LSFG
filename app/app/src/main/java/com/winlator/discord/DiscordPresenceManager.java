package com.winlator.discord;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import androidx.preference.PreferenceManager;
import com.winlator.R;
import com.winlator.XServerDisplayActivity;
import com.winlator.winhandler.OnGetProcessInfoListener;
import com.winlator.winhandler.ProcessInfo;
import com.winlator.xserver.Window;
import com.winlator.xserver.XLock;
import com.winlator.xserver.XServer;
import java.util.ArrayList;
import java.util.List;

/** One owner per display activity. No SDK allocation when disabled or unconfigured. */
public final class DiscordPresenceManager implements OnGetProcessInfoListener {
    public static final String ENABLED = "discord_rpc_enabled";
    public static final String APPLICATION_ID = "discord_application_id";
    private final XServerDisplayActivity activity;
    private final Handler main = new Handler(Looper.getMainLooper());
    private final GameSession session = new GameSession();
    private final List<ProcessInfo> incoming = new ArrayList<>();
    private final String expectedExecutable;
    private final String shortcutName;
    private DiscordPresenceClient client;
    private int status = R.string.discord_disabled;
    private boolean paused;
    private boolean closed;
    private boolean observing;
    private long publishedStart;
    private int publishedPid;
    private String publishedExecutable = "";
    private int expectedCount;
    private int nextIndex;
    private long lastSnapshotMillis;

    public DiscordPresenceManager(XServerDisplayActivity activity, String executable, String name) {
        this.activity = activity;
        expectedExecutable = GameSession.executableName(executable);
        shortcutName = name;
    }

    public static int configurationStatus(Context context, boolean enabled, String id) {
        if (!enabled) return R.string.discord_disabled;
        if (GameSession.applicationId(id) == 0) return R.string.discord_id_required;
        if (!DiscordSdkBridge.isBundled()) return R.string.discord_sdk_missing;
        try { context.getPackageManager().getPackageInfo("com.discord", 0); }
        catch (PackageManager.NameNotFoundException e) { return R.string.discord_app_missing; }
        return R.string.discord_waiting_game;
    }

    public void start() {
        if (closed || observing) return;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        String id = preferences.getString(APPLICATION_ID, "");
        status = configurationStatus(activity, preferences.getBoolean(ENABLED, false), id);
        if (status != R.string.discord_waiting_game) return;
        activity.getWinHandler().addProcessInfoObserver(this);
        observing = true;
        lastSnapshotMillis = SystemClock.elapsedRealtime();
        if (!paused) main.post(pollProcesses);
    }

    private boolean ensureClient() {
        if (client != null) return true;
        try {
            DiscordSdkBridge.initialize(activity);
            String id = PreferenceManager.getDefaultSharedPreferences(activity).getString(APPLICATION_ID, "");
            client = new DiscordPresenceClient(GameSession.applicationId(id), value -> main.post(() -> {
                if (closed) return;
                if (value == R.string.discord_sdk_error) {
                    status = value;
                    main.removeCallbacks(pollProcesses);
                    if (observing) activity.getWinHandler().removeProcessInfoObserver(this);
                    observing = false;
                    client = null; // Transport already clears and destroys the failed SDK client.
                } else if (!paused && publishedPid != 0) status = value;
            }));
            return true;
        } catch (ReflectiveOperationException | RuntimeException | LinkageError e) {
            status = R.string.discord_sdk_error;
            main.removeCallbacks(pollProcesses);
            activity.getWinHandler().removeProcessInfoObserver(this);
            observing = false;
            Log.w("DiscordRPC", "Unable to initialize Android SDK", e);
            return false;
        }
    }

    private final Runnable pollProcesses = new Runnable() {
        @Override public void run() {
            if (closed || paused || !observing) return;
            if (SystemClock.elapsedRealtime() - lastSnapshotMillis >= 15000) processesUnavailable();
            activity.getWinHandler().listProcesses();
            main.postDelayed(this, 5000);
        }
    };

    @Override public void onGetProcessInfo(int index, int count, ProcessInfo process) {
        // Called on WinHandler's receive thread; only publish complete, ordered snapshots.
        if (process == null || count <= 0) {
            incoming.clear();
            nextIndex = 0;
            main.post(this::processesUnavailable);
            return;
        }
        if (index == 0) { incoming.clear(); expectedCount = count; nextIndex = 0; }
        if (count != expectedCount || index != nextIndex) { incoming.clear(); nextIndex = 0; return; }
        incoming.add(process);
        nextIndex++;
        if (nextIndex == count) {
            List<ProcessInfo> snapshot = new ArrayList<>(incoming);
            incoming.clear();
            main.post(() -> acceptProcesses(snapshot));
        }
    }

    private void acceptProcesses(List<ProcessInfo> processes) {
        if (closed || paused || !observing) return;
        lastSnapshotMillis = SystemClock.elapsedRealtime();
        ProcessInfo selected = null;
        XServer server = activity.getXServer();
        try (XLock lock = server.lock(XServer.Lockable.WINDOW_MANAGER)) {
            Window focused = server.windowManager.getFocusedWindow();
            for (Window window = focused; window != null; window = window.getParent()) {
                selected = matchWindow(window, processes);
                if (selected != null) break;
            }
            if (selected == null) {
                for (Window window : server.windowManager.rootWindow.getChildren()) {
                    selected = matchWindow(window, processes);
                    if (selected != null) break;
                }
            }
        }
        // A game may temporarily have no mapped window during fullscreen recreation.
        if (selected == null && session.getPid() != 0) {
            for (ProcessInfo p : processes) {
                if (p.pid == session.getPid() && GameSession.executableName(p.name).equals(session.getExecutable())) {
                    selected = p;
                    break;
                }
            }
        }
        if (selected == null) {
            session.end();
            publishedPid = 0;
            if (client != null) client.clear();
            status = R.string.discord_waiting_game;
            return;
        }
        String executable = GameSession.executableName(selected.name);
        String title = executable.equals(expectedExecutable) ? shortcutName : null;
        if (!ensureClient()) return;
        session.select(selected.pid, executable, title, SystemClock.elapsedRealtime());
        if (publishedPid != session.getPid() || !publishedExecutable.equals(session.getExecutable())) {
            publishedPid = session.getPid();
            publishedExecutable = session.getExecutable();
            publishedStart = session.startSeconds(System.currentTimeMillis(), SystemClock.elapsedRealtime());
        }
        client.update(session.getTitle(), publishedStart);
    }

    private void processesUnavailable() {
        if (closed || paused || !observing) return;
        if (client != null) client.clear();
        publishedPid = 0;
        status = R.string.discord_processes_unavailable;
    }

    private ProcessInfo matchWindow(Window window, List<ProcessInfo> processes) {
        if (!window.isRenderable() || window.isDesktopWindow() || window.getWidth() < 160 || window.getHeight() < 120)
            return null;
        for (ProcessInfo p : processes) {
            // Wine WM_CLASS is also needed when _NET_WM_PID is a Unix rather than Windows PID.
            if (GameSession.matchesWindow(window.getClassName(), window.getProcessId(), p.name, p.pid)) return p;
        }
        return null;
    }

    public String getStatus() { return activity.getString(status); }

    public void pause() {
        paused = true;
        session.pause(SystemClock.elapsedRealtime());
        main.removeCallbacks(pollProcesses);
        if (client != null) client.clear();
        if (observing) status = R.string.discord_paused;
    }

    public void resume() {
        if (closed) return;
        paused = false;
        session.resume(SystemClock.elapsedRealtime());
        publishedPid = 0; // Rebase the timestamp to exclude suspended time.
        if (observing) {
            lastSnapshotMillis = SystemClock.elapsedRealtime();
            status = R.string.discord_waiting_game;
            main.removeCallbacks(pollProcesses);
            main.post(pollProcesses);
        }
    }

    public void close() {
        closed = true;
        main.removeCallbacksAndMessages(null);
        if (observing) activity.getWinHandler().removeProcessInfoObserver(this);
        observing = false;
        if (client != null) { client.close(); client = null; }
        session.end();
    }
}
