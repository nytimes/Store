package com.nytimes.android.external.store3.base

interface DiskWrite<Raw, Key> {
    /**
     * @param key to use to get data from persister
     * If data is not available implementer needs to
     * either return Observable.empty or throw an exception
     */
    suspend fun write(key: Key, raw: Raw): Boolean
}
