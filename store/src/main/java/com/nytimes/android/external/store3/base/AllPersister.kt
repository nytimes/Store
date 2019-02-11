package com.nytimes.android.external.store3.base


import java.io.FileNotFoundException

import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single


interface AllPersister<Raw, Key> : Persister<Raw, Key>, DiskAllRead<Raw>, DiskAllErase {
    /**
     * @param path to use to get data from persister
     * If data is not available implementer needs to
     * throw an exception
     */
    @Throws(FileNotFoundException::class)
    override fun readAll(path: String): Observable<Raw>

    /**
     * @param path to delete all the data in the the path.
     */
    override fun deleteAll(path: String): Observable<Boolean>

    /**
     * @param key to use to get data from persister
     * If data is not available implementer needs to
     * throw an exception
     */
    //    @Override
   override suspend fun read(key: Key): Raw?

    /**
     * @param key to use to store data to persister
     * @param raw     raw string to be stored
     */
    override suspend fun write(key: Key, raw: Raw): Boolean
}
