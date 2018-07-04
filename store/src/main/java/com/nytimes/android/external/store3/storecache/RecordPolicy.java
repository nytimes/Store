package com.nytimes.android.external.store3.storecache;

import java.util.concurrent.TimeUnit;

public enum RecordPolicy {

    ExpireAfterWrite, ExpireAfterAccess;

    public static boolean hasExpired(StoreRecord storeRecord, long nowInMs) {

        long expireTime;

        if (storeRecord.getRecordPolicy() == ExpireAfterWrite) {
            expireTime = storeRecord.getWriteTime();
        } else {
            expireTime = storeRecord.getAccessTime();
        }
        expireTime = expireTime + TimeUnit.MILLISECONDS
                .convert(storeRecord.getTimeDuration(), storeRecord.getTimeUnit());

        return (nowInMs > expireTime);
    }

    public static boolean hasExpired(StoreRecord storeRecord) {
        return hasExpired(storeRecord, System.currentTimeMillis());
    }

}
