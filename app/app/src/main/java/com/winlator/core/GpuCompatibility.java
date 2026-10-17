package com.winlator.core;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * GPU identification only, never a guarantee that a third-party driver or game will work.
 * Kept free of Android/native dependencies so hardware names can be regression-tested.
 */
public final class GpuCompatibility {
    private static final Pattern ADRENO = Pattern.compile(
            "\\badreno(?:\\s*\\(tm\\))?\\s*a?\\s*([0-9]{3})[a-z]?\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern FREEDRENO = Pattern.compile("\\b(?:freedreno\\s+)?FD([67][0-9]{2})\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern MALI_G = Pattern.compile(
            "\\bmali[-\\s]*g([0-9]{2,3})\\b", Pattern.CASE_INSENSITIVE);

    private GpuCompatibility() {}

    public static short adrenoModelId(String renderer) {
        if (renderer == null) return 0;
        Matcher matcher = ADRENO.matcher(renderer);
        if (matcher.find()) return Short.parseShort(matcher.group(1));
        matcher = FREEDRENO.matcher(renderer);
        return matcher.find() ? Short.parseShort(matcher.group(1)) : 0;
    }

    public static int maliGModelId(String renderer) {
        if (renderer == null) return 0;
        Matcher matcher = MALI_G.matcher(renderer);
        return matcher.find() ? Integer.parseInt(matcher.group(1)) : 0;
    }

    public static boolean isAdreno619Or642(String renderer) {
        short model = adrenoModelId(renderer);
        return model == 619 || model == 642;
    }

    public static boolean isMaliG52(String renderer) {
        return maliGModelId(renderer) == 52;
    }

    /** Automatic policy covers A6xx/A7xx only; manual selection remains available. */
    public static boolean isTurnipCandidate(String renderer) {
        int model = adrenoModelId(renderer);
        return model >= 600 && model < 800;
    }

    public static boolean isMali(String renderer) {
        return renderer != null && renderer.toLowerCase(Locale.ROOT).contains("mali");
    }
}
