package com.winlator.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.opengl.EGL14;

import androidx.collection.ArrayMap;
import androidx.preference.PreferenceManager;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

import dalvik.annotation.optimization.CriticalNative;

public abstract class GPUHelper {
    public static int VK_API_VERSION_1_3 = GPUHelper.vkMakeVersion(1, 3, 0);

    static {
        System.loadLibrary("winlator");
    }

    // Do not permanently suppress GPU detection after an initial EGL/Vulkan
    // failure. In-flight calls share one bounded wait; later calls can retry.
    private static final GpuProbeGate identityProbes = new GpuProbeGate(5000);

    private static ArrayMap<String, String> loadGPUInformation(Context context) {
        final ArrayMap<String, String> gpuInfo = new ArrayMap<>();
        gpuInfo.put("renderer", "");
        gpuInfo.put("vendor", "");
        gpuInfo.put("version", "");
        final Context appContext = context.getApplicationContext() != null
                ? context.getApplicationContext() : context;
        final GpuProbeGate.Attempt attempt = identityProbes.acquire(
                android.os.SystemClock.elapsedRealtime());

        if (attempt.owner) {
            Thread probe = new Thread(() -> {
                EGL10 egl = null;
                EGLDisplay display = EGL10.EGL_NO_DISPLAY;
                EGLContext eglContext = EGL10.EGL_NO_CONTEXT;
                javax.microedition.khronos.egl.EGLSurface surface = EGL10.EGL_NO_SURFACE;
                try {
                    String vulkanRenderer = vkGetRenderer();
                    if (vulkanRenderer != null && !vulkanRenderer.isEmpty()) {
                        PreferenceManager.getDefaultSharedPreferences(appContext).edit()
                                .putString("gpu_vulkan_renderer", vulkanRenderer).apply();
                    }
                    egl = (EGL10) EGLContext.getEGL();
                    display = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
                    int[] version = new int[2];
                    if (display == EGL10.EGL_NO_DISPLAY || !egl.eglInitialize(display, version)) return;
                    int[] attributes = {
                            EGL10.EGL_SURFACE_TYPE, EGL10.EGL_PBUFFER_BIT,
                            EGL10.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                            EGL10.EGL_RED_SIZE, 8, EGL10.EGL_GREEN_SIZE, 8,
                            EGL10.EGL_BLUE_SIZE, 8, EGL10.EGL_NONE
                    };
                    EGLConfig[] configs = new EGLConfig[1];
                    int[] count = new int[1];
                    if (!egl.eglChooseConfig(display, attributes, configs, 1, count)
                            || count[0] == 0 || configs[0] == null) return;

                    eglContext = egl.eglCreateContext(display, configs[0], EGL10.EGL_NO_CONTEXT,
                            new int[]{EGL14.EGL_CONTEXT_CLIENT_VERSION, 2, EGL10.EGL_NONE});
                    if (eglContext == EGL10.EGL_NO_CONTEXT) return;
                    surface = egl.eglCreatePbufferSurface(display, configs[0],
                            new int[]{EGL10.EGL_WIDTH, 1, EGL10.EGL_HEIGHT, 1, EGL10.EGL_NONE});
                    if (surface == EGL10.EGL_NO_SURFACE
                            || !egl.eglMakeCurrent(display, surface, surface, eglContext)) return;

                    GL10 gl = (GL10) eglContext.getGL();
                    String renderer = Objects.toString(gl.glGetString(GL10.GL_RENDERER), "");
                    String vendor = Objects.toString(gl.glGetString(GL10.GL_VENDOR), "");
                    String glVersion = Objects.toString(gl.glGetString(GL10.GL_VERSION), "");
                    if (!renderer.isEmpty()) {
                        PreferenceManager.getDefaultSharedPreferences(appContext).edit()
                                .putString("gpu_renderer", renderer)
                                .putString("gpu_vendor", vendor)
                                .putString("gpu_version", glVersion).apply();
                    }
                }
                catch (RuntimeException | LinkageError error) {
                    android.util.Log.w("GPUHelper", "GPU probe failed; system driver remains available", error);
                }
                finally {
                    try {
                        if (egl != null && display != EGL10.EGL_NO_DISPLAY) {
                            egl.eglMakeCurrent(display, EGL10.EGL_NO_SURFACE,
                                    EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
                            if (surface != EGL10.EGL_NO_SURFACE) egl.eglDestroySurface(display, surface);
                            if (eglContext != EGL10.EGL_NO_CONTEXT) egl.eglDestroyContext(display, eglContext);
                            egl.eglTerminate(display);
                        }
                    }
                    catch (RuntimeException | LinkageError cleanupError) {
                        android.util.Log.w("GPUHelper", "GPU probe EGL cleanup failed", cleanupError);
                    }
                    finally {
                        identityProbes.complete(attempt, android.os.SystemClock.elapsedRealtime());
                    }
                }
            }, "aurora-gpu-probe");
            probe.setDaemon(true);
            try {
                probe.start();
            }
            catch (RuntimeException | Error error) {
                identityProbes.complete(attempt, android.os.SystemClock.elapsedRealtime());
                throw error;
            }
        }
        if (attempt.completed != null) {
            try {
                if (!attempt.completed.await(2500, java.util.concurrent.TimeUnit.MILLISECONDS))
                    android.util.Log.w("GPUHelper", "GPU probe timed out; using cached or safe defaults");
            }
            catch (InterruptedException error) {
                Thread.currentThread().interrupt();
            }
        }

        // Read the preferences only after waiting: a joined caller must see
        // the original probe's result rather than an independent empty map.
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(appContext);
        gpuInfo.put("renderer", preferences.getString("gpu_renderer", ""));
        gpuInfo.put("vendor", preferences.getString("gpu_vendor", ""));
        gpuInfo.put("version", preferences.getString("gpu_version", ""));
        return gpuInfo;
    }

    public static String glGetRenderer(Context context) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String value = preferences.getString("gpu_renderer", "");
        if (!value.isEmpty()) return value;

        ArrayMap<String, String> gpuInfo = loadGPUInformation(context);
        return gpuInfo.get("renderer");
    }

    public static String glGetVendor(Context context) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String value = preferences.getString("gpu_vendor", "");
        if (!value.isEmpty()) return value;

        ArrayMap<String, String> gpuInfo = loadGPUInformation(context);
        return gpuInfo.get("vendor");
    }

    public static String glGetVersion(Context context) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String value = preferences.getString("gpu_version", "");
        if (!value.isEmpty()) return value;

        ArrayMap<String, String> gpuInfo = loadGPUInformation(context);
        return gpuInfo.get("version");
    }

    /** Vulkan identity is preferred; GLES remains available for GL version diagnostics. */
    public static String getRenderer(Context context) {
        String renderer = PreferenceManager.getDefaultSharedPreferences(context)
                .getString("gpu_vulkan_renderer", "");
        if (!renderer.isEmpty()) return renderer;
        loadGPUInformation(context);
        String glRenderer = glGetRenderer(context);
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString("gpu_vulkan_renderer", glRenderer);
    }

    public static String resolveTurnipVersion(Context context, String manualVersion) {
        String renderer = getRenderer(context);
        String version = TurnipPolicy.resolveVersion(renderer, manualVersion);
        android.util.Log.i("AuroraGPU", "renderer=" + renderer + " model="
                + GpuCompatibility.adrenoModelId(renderer) + " Turnip=" + version
                + " selection=" + (manualVersion == null || manualVersion.isEmpty() ? "auto" : "manual")
                + " policy=" + TurnipPolicy.REVISION + " reason=" + TurnipPolicy.reason(renderer));
        return version.isEmpty() ? DefaultVersion.TURNIP : version;
    }

    private static native String vkGetRenderer();

    public static short getAdrenoModelId(Context context) {
        return GpuCompatibility.adrenoModelId(getRenderer(context));
    }

    public static int vkMakeVersion(String value) {
        final Pattern pattern = Pattern.compile("([0-9]+)\\.([0-9]+)\\.?([0-9]+)?");
        Matcher matcher = pattern.matcher(value);
        if (matcher.find()) {
            try {
                int major = matcher.group(1) != null ? Integer.parseInt(matcher.group(1)) : 0;
                int minor = matcher.group(2) != null ? Integer.parseInt(matcher.group(2)) : 0;
                int patch = matcher.group(3) != null ? Integer.parseInt(matcher.group(3)) : 0;
                if (matcher.group(1) == null && patch == 0) patch = minor;
                return vkMakeVersion(major, minor, patch);
            }
            catch (NumberFormatException e) {
                return 0;
            }
        }
        else return 0;
    }

    public static int vkMakeVersion(int major, int minor, int patch) {
        return  ((major) << 22) | ((minor) << 12) | (patch);
    }

    public static int vkVersionMajor(int version) {
        return (version) >> 22;
    }

    public static int vkVersionMinor(int version) {
        return ((version) >> 12) & 0x3FF;
    }

    public static int vkVersionPatch(int version) {
        return (version) & 0xFFF;
    }

    public static native String[] vkGetDeviceExtensions();

    @CriticalNative
    public static native int vkGetApiVersion();

    public static native void setGlobalEGLContext();
}
