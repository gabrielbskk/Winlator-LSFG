package com.winlator.box64;

import com.winlator.core.EnvVars;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class Box64ElectronPresetTest {
    @Test public void electronPresetEnablesCefAllocatorCompatibility() {
        EnvVars envVars = Box64PresetManager.getEnvVars(null, Box64Preset.ELECTRON);

        assertEquals("1", envVars.get("BOX64_LIBCEF"));
        assertEquals("2", envVars.get("BOX64_MALLOC_HACK"));
        assertEquals("0", envVars.get("BOX64_DYNAREC_BIGBLOCK"));
        assertEquals("1", envVars.get("BOX64_DYNAREC_STRONGMEM"));
    }

    @Test public void defaultPresetDoesNotEnableElectronWorkarounds() {
        EnvVars envVars = Box64PresetManager.getEnvVars(null, Box64Preset.DEFAULT);

        assertFalse(envVars.has("BOX64_LIBCEF"));
        assertFalse(envVars.has("BOX64_MALLOC_HACK"));
    }
}
