package com.nytimes.android.external.store3.base

interface DiskRead<Raw, Key> {
    suspend fun read(key: Key): Raw?
}
