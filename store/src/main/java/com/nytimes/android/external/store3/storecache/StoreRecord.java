package com.nytimes.android.external.store3.storecache;

import java.util.concurrent.TimeUnit;

public final class StoreRecord<V> {

    private long accessTime = -1;
    private long writeTime = -1;
    private final V value;
    private final long timeDuration;
    private final TimeUnit timeUnit;
    private final RecordPolicy recordPolicy;

    StoreRecord(RecordPolicy recordPolicy, long timeDuration, TimeUnit timeUnit, V value) {
        long nowMs = System.currentTimeMillis();
        this.recordPolicy = recordPolicy;
        this.timeDuration = timeDuration;
        this.timeUnit = timeUnit;
        this.value = value;
        setAccessTime(nowMs);
        setWriteTime(nowMs);
    }

    public V getValue(){
        return value;
    }

    public long getAccessTime() {
        return accessTime;
    }

    public void setAccessTime(long accessTime) {
        this.accessTime = accessTime;
    }

    public long getWriteTime() {
        return writeTime;
    }

    public void setWriteTime(long writeTime) {
        this.writeTime = writeTime;
    }

    public long getTimeDuration() {
        return timeDuration;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public RecordPolicy getRecordPolicy() {
        return recordPolicy;
    }

}
