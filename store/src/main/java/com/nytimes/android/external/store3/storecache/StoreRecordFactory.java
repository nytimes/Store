package com.nytimes.android.external.store3.storecache;

import java.util.concurrent.TimeUnit;

public final class StoreRecordFactory {

    private StoreRecordFactory(){}

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
