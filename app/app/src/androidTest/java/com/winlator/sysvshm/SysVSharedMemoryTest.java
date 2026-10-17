package com.winlator.sysvshm;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import static org.junit.Assert.*;

/** Requires a real/emulated ARM64 Android runtime with libwinlator loaded. */
@RunWith(AndroidJUnit4.class)
public class SysVSharedMemoryTest {
    private int mappings() throws IOException {
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader("/proc/self/maps"))) {
            String line;
            while ((line = reader.readLine()) != null) if (line.contains("sysvshm-")) count++;
        }
        return count;
    }

    @Test public void deletionWaitsForLastAttachmentAndThenUnmaps() throws IOException {
        SysVSharedMemory memory = new SysVSharedMemory();
        int before = mappings();
        try {
            int id = memory.get(4096);
            assertTrue(id > 0);
            ByteBuffer first = memory.attach(id);
            ByteBuffer second = memory.attach(id);
            assertNotNull(first);
            assertSame(first, second);
            assertEquals(before + 1, mappings());
            memory.delete(id);
            assertEquals(-1, memory.getFd(id));
            assertNull(memory.attach(id));
            memory.detach(first);
            assertEquals(before + 1, mappings());
            assertEquals(0, second.get(0)); // Must remain mapped for the second consumer.
            memory.detach(second);
            assertEquals(before, mappings());
            memory.delete(id); // Idempotent cleanup.
        } finally { memory.deleteAll(); }
    }

    @Test public void repeatedAllocateDeleteDoesNotAccumulateMappings() throws IOException {
        SysVSharedMemory memory = new SysVSharedMemory();
        int before = mappings();
        try {
            for (int i = 0; i < 100; i++) {
                int id = memory.get(4096);
                ByteBuffer data = memory.attach(id);
                assertNotNull(data);
                memory.delete(id);
                memory.detach(data);
            }
            assertEquals(before, mappings());
        } finally { memory.deleteAll(); }
    }

    @Test public void shutdownUnmapsAttachedSegments() throws IOException {
        SysVSharedMemory memory = new SysVSharedMemory();
        int before = mappings();
        int id = memory.get(4096);
        assertNotNull(memory.attach(id));
        memory.deleteAll();
        assertEquals(before, mappings());
        assertNull(memory.attach(id));
        memory.deleteAll();
    }
}
