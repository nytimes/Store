package com.nytimes.android.external.store3.base


interface RecordProvider<Key> {
    fun getRecordState(key: Key): RecordState
}
