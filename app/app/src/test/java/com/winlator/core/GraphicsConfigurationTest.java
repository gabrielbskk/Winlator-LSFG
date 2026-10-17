package com.winlator.core;

import com.winlator.container.GraphicsDrivers;
import org.junit.Test;
import static org.junit.Assert.*;

public class GraphicsConfigurationTest {
    @Test public void importedPartialDriverPairsKeepBothSlots() {
        assertArrayEquals(new String[]{"turnip", "gladio"}, GraphicsDrivers.parseIdentifiers("turnip,"));
        assertArrayEquals(new String[]{"vortek", "virgl"}, GraphicsDrivers.parseIdentifiers(",virgl"));
        assertArrayEquals(new String[]{"vortek", "gladio"}, GraphicsDrivers.parseIdentifiers("unknown,unknown"));
    }
    @Test public void emptyExtensionSelectionSurvivesSerialization() {
        KeyValueSet config = new KeyValueSet();
        config.put("exposedDeviceExtensions", "");
        assertEquals("", new KeyValueSet(config.toString()).get("exposedDeviceExtensions", "all"));
        assertEquals("all", new KeyValueSet().get("exposedDeviceExtensions", "all"));
    }
    @Test public void manualVersionAndVulkanSettingsSurviveDriverPairs() {
        String value = "version=24.3.0,exposedDeviceExtensions=VK_KHR_swapchain";
        KeyValueSet[] configs = GraphicsDrivers.parseConfigs("turnip,gladio", value);
        assertEquals("24.3.0", configs[0].get("version"));
        assertEquals("VK_KHR_swapchain", configs[0].get("exposedDeviceExtensions"));
        assertTrue(configs[1].isEmpty());
        configs = GraphicsDrivers.parseConfigs("vortek,virgl", "exposedDeviceExtensions=|vtest=1");
        assertEquals("", configs[0].get("exposedDeviceExtensions", "all"));
        assertEquals("1", configs[1].get("vtest"));
    }
}
