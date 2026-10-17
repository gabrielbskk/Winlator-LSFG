package com.winlator.core;

import com.winlator.container.GraphicsDrivers;
import org.junit.Test;
import static org.junit.Assert.*;

/** Manual driver/version regression tests, including old auto/empty config migration. */
public class TurnipVersionSelectionTest {
    @Test public void manualVersionIsNeverOverriddenByDetectedGpu() {
        assertEquals("24.3.4.1", TurnipVersionSelection.resolve("24.3.4.1"));
        assertEquals("26.2.1.1", TurnipVersionSelection.resolve(" 26.2.1.1 "));
        assertEquals("25.0.0.1", TurnipVersionSelection.resolve("25.0.0.1"));
        assertEquals("custom-version", TurnipVersionSelection.resolve("custom-version"));
    }

    @Test public void legacyAutomaticOrMissingValuesUseFixedBundledFallback() {
        for (String oldValue : new String[]{null, "", "  ", "auto", "AUTO"}) {
            assertEquals(DefaultVersion.TURNIP, TurnipVersionSelection.resolve(oldValue));
        }
    }

    @Test public void newContainersNeverAutoSelectTurnip() {
        assertEquals("vortek,gladio", GraphicsDrivers.getDefaultDriver(null));
        assertArrayEquals(new String[]{"turnip", "gladio"},
                GraphicsDrivers.parseIdentifiers("turnip,gladio"));
        assertArrayEquals(new String[]{"vortek", "gladio"},
                GraphicsDrivers.parseIdentifiers(null));
    }
}
