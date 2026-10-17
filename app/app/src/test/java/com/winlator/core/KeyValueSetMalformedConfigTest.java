package com.winlator.core;

import com.winlator.container.GraphicsDrivers;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.junit.Test;
import static org.junit.Assert.*;

/** Regression checks for malformed legacy and imported graphics configurations. */
public class KeyValueSetMalformedConfigTest {
    @Test public void malformedFieldsAreIgnoredWithoutDestroyingTheSavedConfig() {
        String imported = "broken,version=26.1.0,,bad,key=hello=world,exposedDeviceExtensions=";
        KeyValueSet config = new KeyValueSet(imported);
        assertEquals("26.1.0", config.get("version"));
        assertEquals("hello=world", config.get("key"));
        assertEquals("", config.get("exposedDeviceExtensions", "all"));
        assertEquals("fallback", config.get("missing", "fallback"));
        config.put("version", "24.3.4.1");
        assertEquals("24.3.4.1", config.get("version"));
        assertTrue(config.toString().contains("broken,"));
        assertTrue(config.toString().contains(",bad,"));
    }

    @Test public void iteratorSkipsBadSegmentsAndKeepsEmptyValues() {
        KeyValueSet config = new KeyValueSet("invalid,,a=1,also-invalid,b=,c=three=four,");
        ArrayList<String> entries = new ArrayList<>();
        for (String[] field : config) entries.add(field[0] + "=" + field[1]);
        assertArrayEquals(new String[]{"a=1", "b=", "c=three=four"}, entries.toArray(new String[0]));
        Iterator<String[]> exhausted = new KeyValueSet("invalid").iterator();
        assertFalse(exhausted.hasNext());
        try {
            exhausted.next();
            fail("Expected an exhausted iterator to throw");
        } catch (NoSuchElementException expected) {
            // Iterator contract.
        }
    }

    @Test public void malformedImportedDriverSettingsDoNotCrashParsing() {
        KeyValueSet[] configs = GraphicsDrivers.parseConfigs(
                "turnip,gladio", "invalid,version=24.3.4.1|bad,option=manual");
        assertEquals("24.3.4.1", configs[0].get("version"));
        assertEquals("manual", configs[1].get("option"));
        assertEquals("vortek,gladio", GraphicsDrivers.getDefaultDriver(null));
    }
}
