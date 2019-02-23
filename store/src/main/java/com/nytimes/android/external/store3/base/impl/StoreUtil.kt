package com.nytimes.android.external.store3.base.impl

import com.nytimes.android.external.store3.base.BasePersister
import com.nytimes.android.external.store3.base.Clearable
import com.nytimes.android.external.store3.base.RecordProvider
import com.nytimes.android.external.store3.base.RecordState.STALE

object StoreUtil {

    fun <Raw, Key> shouldReturnNetworkBeforeStale(
            persister: BasePersister, stalePolicy: StalePolicy, key: Key): Boolean {
        return stalePolicy == StalePolicy.NETWORK_BEFORE_STALE && persisterIsStale<Any, Key>(key, persister)
    }

    fun <Raw, Key> persisterIsStale(key: Key, persister: BasePersister): Boolean {
        if (persister is RecordProvider<*>) {
            val provider = persister as RecordProvider<Key>
            val recordState = provider.getRecordState(key)
            return recordState == STALE
        }
        return false
    }

    fun <Raw, Key> clearPersister(persister: BasePersister, key: Key) {
        val isPersisterClearable = persister is Clearable<*>

        if (isPersisterClearable) {
            (persister as Clearable<Key>).clear(key)
        }
    }
}
