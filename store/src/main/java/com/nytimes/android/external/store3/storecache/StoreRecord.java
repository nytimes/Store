package com.nytimes.android.external.store3.storecache;

import java.util.concurrent.TimeUnit;

public final class StoreRecord<V> {

    private long accessTime = -1;
    private long writeTime = -1;
    private long timeDuration = 1;
    private TimeUnit timeUnit = TimeUnit.MINUTES;
    private RecordPolicy recordPolicy = RecordPolicy.ExpireAfterWrite;
    private V value;

    private StoreRecord() {}

    public V getValue(){
        return value;
    }
    public void setValue(V value) {
        this.value = value;
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

    public void setTimeDuration(long timeDuration) {
        this.timeDuration = timeDuration;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public RecordPolicy getRecordPolicy() {
        return recordPolicy;
    }

    public void setRecordPolicy(RecordPolicy recordPolicy) {
        this.recordPolicy = recordPolicy;
    }

    public static StoreRecord create(long duration, TimeUnit timeUnit) {
        return create(duration, timeUnit, System.currentTimeMillis());
    }

    public static StoreRecord create(long duration, TimeUnit timeUnit, long nowMs) {
        StoreRecord record = new StoreRecord();
        record.setAccessTime(nowMs);
        record.setWriteTime(nowMs);
        record.setTimeDuration(duration);
        record.setTimeUnit(timeUnit);
        return record;
    }
}
