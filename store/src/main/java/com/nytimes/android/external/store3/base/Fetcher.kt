package com.nytimes.android.external.store3.base

import io.reactivex.Observable
import io.reactivex.Single


/**
 * Interface for fetching new data for a Store
 *
 * @param <Raw> data type before parsing
</Raw> */
interface Fetcher<Raw, Key> {

    /**
     * @param key Container with Key and Type used as a request param
     * @return Observable that emits [Raw] data
     */
    suspend fun fetch(key: Key):Raw
}
