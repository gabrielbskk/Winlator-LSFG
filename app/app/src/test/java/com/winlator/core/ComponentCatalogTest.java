package com.winlator.core;

import org.junit.Test;
import static org.junit.Assert.*;

public class ComponentCatalogTest {
    @Test public void retainsAllowedVersionsAndDeduplicatesCRLFCatalog() {
        String raw = "turnip-26.1.0.tzst\r\nturnip-24.3.4.1.tzst\n"
                + "turnip-26.1.0.tzst\n\n# comment\n";
        assertArrayEquals(new String[]{"turnip-26.1.0.tzst", "turnip-24.3.4.1.tzst"},
                ComponentCatalog.validFileNames("turnip", raw));
        assertTrue(ComponentCatalog.isSafeFileName("vkd3d", "vkd3d-3.0b.tzst"));
        assertTrue(ComponentCatalog.isSafeFileName("dxvk", "dxvk-2.6.1.tzst"));
    }

    @Test public void rejectsTraversalWrongTypesAndIncompleteDownloads() {
        for (String unsafe : new String[]{
                "../turnip-26.1.0.tzst", "turnip-../evil.tzst",
                "turnip-../../x.tzst", "turnip-/other.tzst",
                "turnip-..\\\\escape.tzst", "turnip-.tzst",
                "turnip-26.1.0.tzst.partial", "turnip-26.1.0.zip",
                "dxvk-26.1.0.tzst", "turnip-26.1.0.tzst?token=123",
                "turnip-26.1.0.tzst\\u0000"
        }) assertFalse(unsafe, ComponentCatalog.isSafeFileName("turnip", unsafe));
        assertFalse(ComponentCatalog.isSafeFileName("../turnip", "turnip-26.1.0.tzst"));
        assertFalse(ComponentCatalog.isSafeFileName("turnip", null));
        assertArrayEquals(new String[0], ComponentCatalog.validFileNames("turnip", null));
    }

    @Test public void corruptCatalogCannotExposeAnyDownload() {
        String raw = "turnip-../../steal.tzst\nturnip-26.0.3.tzst.tmp\n"
                + "dxvk-2.4.1.tzst\n/absolute.tzst\n";
        assertEquals(0, ComponentCatalog.validFileNames("turnip", raw).length);
    }
}
