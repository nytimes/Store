package com.nytimes.android.external.store3.base

import io.reactivex.Maybe

interface DiskRead<Raw, Key> {
    suspend fun read(key: Key): Raw?
}
