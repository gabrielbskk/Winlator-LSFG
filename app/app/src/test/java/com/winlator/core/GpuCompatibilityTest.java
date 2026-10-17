package com.winlator.core;

import static org.junit.Assert.*;
import org.junit.Test;

public class GpuCompatibilityTest {
    @Test public void adreno619IsRecognized() {
        assertEquals(619, GpuCompatibility.adrenoModelId("Adreno (TM) 619"));
        assertTrue(GpuCompatibility.isAdreno619Or642("Qualcomm Adreno 619"));
        assertTrue(GpuCompatibility.isTurnipCandidate("Adreno 619"));
    }
    @Test public void adreno642LIsRecognized() {
        assertEquals(642, GpuCompatibility.adrenoModelId("Adreno (TM) 642L"));
        assertTrue(GpuCompatibility.isAdreno619Or642("Adreno 642L"));
        assertTrue(GpuCompatibility.isTurnipCandidate("Adreno 642"));
    }
    @Test public void maliUsesSystemDriverNotTurnip() {
        assertTrue(GpuCompatibility.isMaliG52("Mali-G52 MP2"));
        assertEquals(52, GpuCompatibility.maliGModelId("ARM Mali G52"));
        assertTrue(GpuCompatibility.isMali("ARM Mali-G52"));
        assertFalse(GpuCompatibility.isTurnipCandidate("Mali-G52"));
    }
    @Test public void unrelatedRendererIsNotAdreno() {
        assertEquals(0, GpuCompatibility.adrenoModelId(null));
        assertEquals(0, GpuCompatibility.adrenoModelId("SwiftShader 6.1"));
        assertEquals(0, GpuCompatibility.maliGModelId("Adreno 619"));
        assertFalse(GpuCompatibility.isTurnipCandidate("Mali-G57"));
    }
}
