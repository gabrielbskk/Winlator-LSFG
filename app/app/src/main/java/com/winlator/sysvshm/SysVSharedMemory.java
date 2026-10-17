package com.winlator.sysvshm;

import android.util.SparseArray;

import com.winlator.xconnector.XConnectorEpoll;

import java.nio.ByteBuffer;

public class SysVSharedMemory {
    private final SparseArray<SHMemory> shmemories = new SparseArray<>();
    private int maxSHMemoryId = 0;

    static {
        System.loadLibrary("winlator");
    }

    private static class SHMemory {
        private int fd;
        private long size;
        private ByteBuffer data;
        private int attachments;
        private boolean deleted;
    }

    public int getFd(int shmid) {
        synchronized (shmemories) {
            SHMemory shmemory = shmemories.get(shmid);
            return shmemory != null && !shmemory.deleted ? shmemory.fd : -1;
        }
    }

    public int get(long size) {
        synchronized (shmemories) {
            if (size <= 0 || maxSHMemoryId == Integer.MAX_VALUE) return -1;
            int index = shmemories.size();
            int fd = ashmemCreateRegion(index, size);
            if (fd < 0) return -1;

            SHMemory shmemory = new SHMemory();
            int id = ++maxSHMemoryId;
            shmemory.fd = fd;
            shmemory.size = size;
            shmemories.put(id, shmemory);
            return id;
        }
    }

    public void delete(int shmid) {
        synchronized (shmemories) {
            SHMemory shmemory = shmemories.get(shmid);
            if (shmemory != null) {
                if (shmemory.fd != -1) {
                    XConnectorEpoll.closeFd(shmemory.fd);
                    shmemory.fd = -1;
                }
                // IPC_RMID must not invalidate an existing client's DirectByteBuffer.
                shmemory.deleted = true;
                if (shmemory.attachments == 0) release(shmid, shmemory);
            }
        }
    }

    public void deleteAll() {
        synchronized (shmemories) {
            // Called only after guest and X server users have stopped.
            for (int i = shmemories.size() - 1; i >= 0; i--) {
                SHMemory shmemory = shmemories.valueAt(i);
                if (shmemory.fd != -1) XConnectorEpoll.closeFd(shmemory.fd);
                release(shmemories.keyAt(i), shmemory);
            }
        }
    }

    public ByteBuffer attach(int shmid) {
        synchronized (shmemories) {
            SHMemory shmemory = shmemories.get(shmid);
            if (shmemory != null && !shmemory.deleted) {
                if (shmemory.data == null) shmemory.data = mapSHMSegment(shmemory.fd, shmemory.size, 0, true);
                if (shmemory.data != null) shmemory.attachments++;
                return shmemory.data;
            }
            else return null;
        }
    }

    public void detach(ByteBuffer data) {
        synchronized (shmemories) {
            for (int i = 0; i < shmemories.size(); i++) {
                SHMemory shmemory = shmemories.valueAt(i);
                if (data != null && shmemory.data == data) {
                    if (shmemory.attachments > 0) shmemory.attachments--;
                    if (shmemory.attachments == 0) {
                        unmapSHMSegment(shmemory.data, shmemory.size);
                        shmemory.data = null;
                        if (shmemory.deleted) shmemories.removeAt(i);
                    }
                    break;
                }
            }
        }
    }

    private void release(int shmid, SHMemory shmemory) {
        if (shmemory.data != null) unmapSHMSegment(shmemory.data, shmemory.size);
        shmemory.data = null;
        shmemory.attachments = 0;
        shmemories.remove(shmid);
    }

    public static native int createMemoryFd(String name, int size);

    private static native int ashmemCreateRegion(int index, long size);

    public static native ByteBuffer mapSHMSegment(int fd, long size, int offset, boolean readonly);

    public static native void unmapSHMSegment(ByteBuffer data, long size);
}
