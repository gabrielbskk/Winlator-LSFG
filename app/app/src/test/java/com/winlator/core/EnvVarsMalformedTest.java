package com.winlator.core;

import org.junit.Test;
import static org.junit.Assert.*;

public class EnvVarsMalformedTest {
    @Test public void malformedImportedTokenDoesNotCrashOrDiscardFollowingSettings() {
        EnvVars variables = new EnvVars("TU_DEBUG=noconform invalid MESA_VK_WSI_USE_HWBUF=1"
                + " =bad DXVK_LOG_LEVEL=none");
        assertEquals("noconform", variables.get("TU_DEBUG"));
        assertEquals("1", variables.get("MESA_VK_WSI_USE_HWBUF"));
        assertEquals("none", variables.get("DXVK_LOG_LEVEL"));
        assertFalse(variables.has("invalid"));
        assertFalse(variables.has(""));
    }

    @Test public void emptyOrNullSegmentsAreSkippedAndEqualsInValueIsPreserved() {
        EnvVars variables = new EnvVars();
        variables.putAll(new String[]{null, "", "broken", "=bad", "KEY=", "PATH=a=b=c", "KEY=restored"});
        assertEquals("restored", variables.get("KEY"));
        assertEquals("a=b=c", variables.get("PATH"));
        assertFalse(variables.has(""));
    }

    @Test public void manualDriverVariablesSurviveRoundtrip() {
        EnvVars original = new EnvVars("TU_DEBUG=gmem MESA_VK_WSI_USE_HWBUF=0"
                + " unexpected MESA_SHADER_CACHE_DISABLE=false");
        EnvVars restored = new EnvVars(original.toString());
        assertEquals("gmem", restored.get("TU_DEBUG"));
        assertEquals("0", restored.get("MESA_VK_WSI_USE_HWBUF"));
        assertEquals("false", restored.get("MESA_SHADER_CACHE_DISABLE"));
    }
}
