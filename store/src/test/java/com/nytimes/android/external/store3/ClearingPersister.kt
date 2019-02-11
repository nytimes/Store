package com.nytimes.android.external.store3

import com.nytimes.android.external.store3.base.Clearable
import com.nytimes.android.external.store3.base.Persister
import com.nytimes.android.external.store3.base.impl.BarCode

open class ClearingPersister : Persister<Int, BarCode>, Clearable<BarCode> {
    override suspend fun read(key: BarCode): Int? {
        throw RuntimeException()
    }

    override suspend fun write(key: BarCode, raw: Int): Boolean {
        throw RuntimeException()
    }

    override fun clear(key: BarCode) {
        throw RuntimeException()
    }
}
