package com.winlator.core;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Downloads into a private staging file in the destination directory.
 * A truncated, cancelled or failed download cannot erase an installed driver.
 * The final replacement must be atomic; if unsupported, keep the old file.
 * Android minSdk 26 supports the java.nio.file API used here.
 */
public final class AtomicDownload {
    private AtomicDownload() {}

    public static boolean write(InputStream input, File destination, long expectedBytes,
                                AtomicBoolean cancelled, Callback<Integer> onProgress) {
        if (input == null || destination == null || cancelled == null || cancelled.get()) return false;
        File parent = destination.getAbsoluteFile().getParentFile();
        if (parent == null || (!parent.isDirectory() && !parent.mkdirs())) return false;
        File staged = null;
        try {
            staged = File.createTempFile(".aurora-download-", ".partial", parent);
            long written = 0;
            try (FileOutputStream output = new FileOutputStream(staged);
                 BufferedOutputStream buffered = new BufferedOutputStream(output, 32768)) {
                byte[] block = new byte[32768];
                int count;
                while (!cancelled.get() && (count = input.read(block)) != -1) {
                    buffered.write(block, 0, count);
                    written += count;
                    if (expectedBytes > 0 && onProgress != null) {
                        onProgress.call((int)Math.min(100L, written * 100L / expectedBytes));
                    }
                }
                buffered.flush();
                output.getFD().sync();
            }
            // A transfer with unknown length must contain at least one byte.
            if (cancelled.get() || written == 0 || (expectedBytes >= 0 && written != expectedBytes)) return false;
            Files.move(staged.toPath(), destination.toPath(),
                    StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            return true;
        }
        catch (IOException | SecurityException error) {
            return false;
        }
        finally {
            if (staged != null && staged.isFile()) staged.delete();
        }
    }
}
