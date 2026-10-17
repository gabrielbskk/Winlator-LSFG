package com.winlator.discord;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.util.Log;
import com.winlator.R;
import java.util.function.IntConsumer;

/** Serial SDK access, bounded retries, and no work in the rendering thread. */
final class DiscordPresenceClient {
    private final HandlerThread thread = new HandlerThread("DiscordPresence");
    private final Handler handler;
    private final IntConsumer status;
    private long handle;
    private String title;
    private String artwork = "";
    private long startSeconds;
    private long nextAttempt;
    private long attemptStarted;
    private long retryDelay = 5000;
    private int result;
    private boolean closed;

    DiscordPresenceClient(long applicationId, IntConsumer status) {
        this.status = status;
        thread.start();
        handler = new Handler(thread.getLooper());
        handler.post(() -> {
            try {
                handle = DiscordSdkBridge.create(applicationId);
                if (handle == 0) throw new IllegalStateException("Discord client was not created");
                handler.post(tick);
            } catch (RuntimeException | LinkageError e) { failed(e); }
        });
    }

    void update(String name, long start, String image) {
        handler.post(() -> {
            String selectedArtwork = DiscordArtwork.normalize(image);
            if (closed || (name.equals(title) && start == startSeconds &&
                           selectedArtwork.equals(artwork))) return;
            title = name;
            artwork = selectedArtwork;
            startSeconds = start;
            result = 0;
            nextAttempt = 0;
            retryDelay = 5000;
        });
    }

    void clear() {
        handler.post(() -> {
            if (closed || title == null) return;
            title = null;
            result = 0;
            try { if (handle != 0) DiscordSdkBridge.clear(handle); }
            catch (RuntimeException | LinkageError e) { failed(e); }
        });
    }

    private final Runnable tick = new Runnable() {
        @Override public void run() {
            if (closed || handle == 0) return;
            try {
                long now = SystemClock.elapsedRealtime();
                // Reassert infrequently so restarting Discord can recover the same session.
                if (title != null && result == 2 && now >= nextAttempt) result = 0;
                if (title != null && result != 1 && result != 2 && now >= nextAttempt) {
                    DiscordSdkBridge.update(handle, title, startSeconds, artwork);
                    result = 1;
                    attemptStarted = now;
                    status.accept(R.string.discord_connecting);
                }
                int polled = DiscordSdkBridge.poll(handle);
                if (title != null && result == 1) {
                    if (polled == 2) {
                        result = 2;
                        retryDelay = 5000;
                        nextAttempt = now + 60000;
                        status.accept(R.string.discord_published);
                    } else if (polled == 3 || now - attemptStarted >= 15000) {
                        result = 3;
                        nextAttempt = now + retryDelay;
                        retryDelay = Math.min(60000, retryDelay * 2);
                        status.accept(R.string.discord_retrying);
                    }
                }
                // Timestamp rendering is done by Discord, not by per-second RPC updates.
                handler.postDelayed(this, PresencePollingPolicy.delayMillis(result, title != null));
            } catch (RuntimeException | LinkageError e) { failed(e); }
        }
    };

    private void failed(Throwable error) {
        Log.w("DiscordRPC", "Discord integration unavailable; gameplay is unaffected", error);
        status.accept(R.string.discord_sdk_error);
        closeOnWorker();
    }

    void close() { handler.post(this::closeOnWorker); }

    private void closeOnWorker() {
        if (closed) return;
        closed = true;
        handler.removeCallbacksAndMessages(null);
        try { if (handle != 0) DiscordSdkBridge.clear(handle); }
        catch (RuntimeException | LinkageError ignored) {}
        // Let the asynchronous RPC clear leave the SDK before destroying its client.
        handler.postDelayed(() -> {
            try { if (handle != 0) DiscordSdkBridge.poll(handle); }
            catch (RuntimeException | LinkageError ignored) {}
            finally {
                try {
                    if (handle != 0) DiscordSdkBridge.destroy(handle);
                } catch (RuntimeException | LinkageError e) {
                    Log.w("DiscordRPC", "Could not destroy the Discord SDK client", e);
                } finally {
                    handle = 0;
                    thread.quitSafely();
                }
            }
        }, 500);
    }
}
