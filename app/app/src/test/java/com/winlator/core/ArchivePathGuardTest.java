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

    @Test public void onlyArm64SharedLibrariesAreAccepted() throws Exception {
        File file = File.createTempFile("aurora-elf", ".so");
        try {
            byte[] elf = new byte[20];
            elf[0] = 0x7f; elf[1] = 'E'; elf[2] = 'L'; elf[3] = 'F';
            elf[4] = 2; elf[5] = 1; // 64-bit little-endian
            elf[16] = 3; // shared object (ET_DYN)
            elf[18] = (byte)183; // AArch64
            Files.write(file.toPath(), elf);
            assertTrue(ArchivePathGuard.isArm64SharedLibrary(file));
            elf[4] = 1; // ELF32
            Files.write(file.toPath(), elf);
            assertFalse(ArchivePathGuard.isArm64SharedLibrary(file));
            elf[4] = 2; elf[18] = 62; // x86-64
            Files.write(file.toPath(), elf);
            assertFalse(ArchivePathGuard.isArm64SharedLibrary(file));
            elf[18] = (byte)183; elf[0] = 0; // wrong format
            Files.write(file.toPath(), elf);
            assertFalse(ArchivePathGuard.isArm64SharedLibrary(file));
        } finally {
            file.delete();
        }
    }

    @Test public void rejectsDangerousComponentNames() {
        for (String name : new String[]{"", "..", "../driver", "driver/../x",
                "driver\\\\evil", ".hidden", "   ", "driver..rollback", "a/b"})
            assertFalse(name, ArchivePathGuard.safeDriverLabel(name));
        assertTrue(ArchivePathGuard.safeDriverLabel("Adreno 619 Custom Driver"));
        assertTrue(ArchivePathGuard.safeDriverLabel("mali-g52-v1.0"));
    }
}
