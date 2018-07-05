package com.nytimes.android.external.store3.storecache;

public class WriterLock {

    private int givenLocks = 0;
    private final Object mutex = new Object();

    public void getWriteLock() {
        synchronized (mutex) {
            while (givenLocks != 0) {
                try {
                    mutex.wait();
                } catch (InterruptedException e) {}
            }
            givenLocks = -1;
        }
    }

    public void releaseLock() {
        synchronized (mutex) {
            if (givenLocks == 0) {
                return;
            } else if (givenLocks == -1) {
                givenLocks = 0;
            } else {
                givenLocks--;
            }
            mutex.notifyAll();
        }
    }
}
