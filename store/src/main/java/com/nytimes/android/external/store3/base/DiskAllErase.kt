package com.nytimes.android.external.store3.base


interface DiskAllErase {
    /**
     * @param path to use to delete all files
     */
    suspend fun deleteAll(path: String): Boolean
}
