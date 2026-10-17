package com.winlator.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.junit.Test;
import static org.junit.Assert.*;

public class ArchivePathGuardTest {
    @Test public void permitsRelativeDriverMembersAndLibraries() throws Exception {
        File root = Files.createTempDirectory("aurora-archive").toFile();
        try {
            assertEquals(new File(root, "lib/libvulkan.so").getCanonicalFile(),
                    ArchivePathGuard.resolveInside(root, "lib/libvulkan.so"));
            assertEquals(new File(root, "driver.json").getCanonicalFile(),
                    ArchivePathGuard.resolveInside(root, "driver.json"));
            assertTrue(ArchivePathGuard.symlinkTargetInside(root,
                    new File(root, "lib/link.so"), "libvulkan.so"));
            assertTrue(ArchivePathGuard.safeDriverLabel("Turnip v26.1 (custom)"));
        } finally { root.delete(); }
    }

    @Test public void rejectsTraversalAbsoluteAndBackslashArchivePaths() throws Exception {
        File root = Files.createTempDirectory("aurora-archive").toFile();
        try {
            for (String name : new String[]{"../outside", "lib/../../escape",
                    "/absolute/path", "nested//name", "C:/windows/system32",
                    "lib\\\\evil.dll", "", "../"}) {
                try {
                    ArchivePathGuard.resolveInside(root, name);
                    fail("Allowed unsafe archive path: " + name);
                } catch (IOException expected) { /* expected */ }
            }
            assertFalse(ArchivePathGuard.symlinkTargetInside(root,
                    new File(root, "lib/link.so"), "../../outside"));
            assertFalse(ArchivePathGuard.symlinkTargetInside(root,
                    new File(root, "lib/link.so"), "/system/lib.so"));
        } finally { root.delete(); }
    }

    @Test public void rejectsDangerousComponentNames() {
        for (String name : new String[]{"", "..", "../driver", "driver/../x",
                "driver\\\\evil", ".hidden", "   ", "driver..rollback", "a/b"})
            assertFalse(name, ArchivePathGuard.safeDriverLabel(name));
        assertTrue(ArchivePathGuard.safeDriverLabel("Adreno 619 Custom Driver"));
        assertTrue(ArchivePathGuard.safeDriverLabel("mali-g52-v1.0"));
    }
}
