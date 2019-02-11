package com.nytimes.android.external.store3.base

import com.sun.org.apache.xpath.internal.operations.Bool
import io.reactivex.Single

interface DiskWrite<Raw, Key> {
    /**
     * @param key to use to get data from persister
     * If data is not available implementer needs to
     * either return Observable.empty or throw an exception
     */
   suspend fun write(key: Key, raw: Raw): Boolean
}
