package com.winlator.core;

import android.util.Log;

import java.io.File;

/** Imports redistributable-at-runtime fonts from the device, without bundling them in the APK. */
public final class AndroidFontInstaller {
    private static final String TAG = "AndroidFontInstaller";
    private static final String[] FALLBACK_FONTS = {
        "NotoSansCJK-Regular.ttc",
        "NotoColorEmoji.ttf",
        "NotoColorEmojiFlags.ttf",
        "NotoSansSymbols-Regular-Subsetted.ttf",
        "NotoSansSymbols-Regular-Subsetted2.ttf"
    };

    private AndroidFontInstaller() {}

    public static int installMissingSystemFonts(File containerRoot) {
        if (containerRoot == null) return 0;
        File destination = new File(containerRoot, ".wine/drive_c/windows/Fonts");
        int installed = 0;
        for (String name : FALLBACK_FONTS) {
            File source = new File("/system/fonts", name);
            File target = new File(destination, name);
            if (!source.isFile() || target.isFile()) continue;
            if (FileUtils.copy(source, target)) installed++;
            else Log.w(TAG, "Could not import Android system font: " + name);
        }
        if (installed > 0) Log.i(TAG, "Imported " + installed + " Android fallback fonts into Wine");
        return installed;
    }
}
