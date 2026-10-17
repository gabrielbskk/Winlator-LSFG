package com.winlator.core;

import org.junit.Test;
import static org.junit.Assert.*;

public class VortekConfigLimitsTest {
    @Test public void preservesValidSettings() {
        assertEquals(0, VortekConfigLimits.memoryLimitMB(0));
        assertEquals(12288, VortekConfigLimits.memoryLimitMB(12288));
        assertEquals(6144, VortekConfigLimits.memoryLimitMB(6144));
        assertEquals(64, VortekConfigLimits.imageCacheMB(64));
        assertEquals(1024, VortekConfigLimits.imageCacheMB(1024));
        assertEquals(3, VortekConfigLimits.resourceMemoryType(3));
    }

    @Test public void rejectsNegativeAndOverflowingImportedValues() {
        assertEquals(0, VortekConfigLimits.memoryLimitMB(-1));
        assertEquals(0, VortekConfigLimits.memoryLimitMB(65535));
        assertEquals(256, VortekConfigLimits.imageCacheMB(-999));
        assertEquals(256, VortekConfigLimits.imageCacheMB(65535));
        assertEquals(0, VortekConfigLimits.resourceMemoryType(-1));
        assertEquals(0, VortekConfigLimits.resourceMemoryType(259));
    }

    @Test public void acceptsExplicitUnlistedButBoundedCustomSizes() {
        assertEquals(1536, VortekConfigLimits.memoryLimitMB(1536));
        assertEquals(333, VortekConfigLimits.imageCacheMB(333));
    }
}
