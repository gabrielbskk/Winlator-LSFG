package com.winlator.core;

import org.junit.Test;
import static org.junit.Assert.*;

public class TurnipPolicyTest {
    @Test public void coversBothAdrenoGenerationsAndVariants() {
        for (int model : new int[]{619, 642, 643, 650, 660, 680, 685, 690,
                710, 720, 725, 730, 732, 735, 740, 750, 699, 799}) {
            for (String prefix : new String[]{"Qualcomm Adreno (TM) ", "adreno A", "FD"}) {
                String renderer = prefix + model;
                assertEquals(model, GpuCompatibility.adrenoModelId(renderer));
                assertTrue(GpuCompatibility.isTurnipCandidate(renderer));
                assertEquals("26.1.0", TurnipPolicy.recommendedVersion(renderer));
            }
        }
        assertEquals(642, GpuCompatibility.adrenoModelId(" Adreno (tm)  642L "));
    }
    @Test public void maliAndUnknownNeverReceiveAutomaticTurnip() {
        for (String renderer : new String[]{null, "", "SwiftShader", "Adreno 530",
                "Adreno A830", "Mali-G52 MP2", "ARM Mali G57", "Mali-G68", "Mali-G72", "Mali-G76"}) {
            assertFalse(GpuCompatibility.isTurnipCandidate(renderer));
            assertEquals("", TurnipPolicy.recommendedVersion(renderer));
        }
        assertEquals(530, GpuCompatibility.adrenoModelId("Adreno (TM) 530"));
        assertEquals(57, GpuCompatibility.maliGModelId("ARM Mali-G57 MC2"));
        assertEquals(68, GpuCompatibility.maliGModelId("Mali G68"));
    }
    @Test public void manualOverrideSurvivesPolicyAndAutomaticCanBeRestored() {
        assertEquals("24.3.0", TurnipPolicy.resolveVersion("Adreno 740", "24.3.0"));
        assertEquals("26.1.0", TurnipPolicy.resolveVersion("Adreno 740", "auto"));
        assertEquals("26.1.0", TurnipPolicy.resolveVersion("Adreno 619", ""));
        assertNotEquals(TurnipPolicy.reason("Adreno 619"), TurnipPolicy.reason("Adreno 740"));
    }
}
