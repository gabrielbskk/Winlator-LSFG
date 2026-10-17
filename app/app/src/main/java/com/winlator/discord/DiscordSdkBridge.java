package com.winlator.discord;

import android.app.Activity;
import androidx.annotation.Keep;
import com.winlator.BuildConfig;

/** Android/Bionic bridge, deliberately separate from the guest glibc runtime. */
@Keep
public final class DiscordSdkBridge {
    private DiscordSdkBridge() {}

    public static boolean isBundled() { return BuildConfig.DISCORD_SDK_ENABLED; }

    public static void initialize(Activity activity) throws ReflectiveOperationException {
        if (!isBundled()) throw new IllegalStateException("Discord Social SDK not bundled");
        System.loadLibrary("winlator_discord");
        Class.forName("com.discord.socialsdk.DiscordSocialSdkInit")
            .getMethod("setEngineActivity", Activity.class).invoke(null, activity);
    }

    static native long create(long applicationId);
    static native void update(long handle, String title, long startSeconds, String artwork);
    static native int poll(long handle);
    static native void clear(long handle);
    static native void destroy(long handle);
}
