package com.nytimes.android.external.store3.base


interface DiskErase<Raw, Key> {
    /**
     * @param key to use to delete a particular file using persister
     */
    suspend fun delete(key: Key): Boolean
}
