package com.nytimes.android.sample

import com.nytimes.android.external.store3.base.impl.BarCode
import com.nytimes.android.external.store3.base.impl.StoreBuilder
import kotlinx.coroutines.runBlocking
import org.junit.Test

class NoNetworkTest {
    private val store = StoreBuilder.barcode<Any>()
            .fetcher { throw EXCEPTION }
            .open()

    @Test(expected = java.lang.Exception::class)
    fun testNoNetwork() = runBlocking<Unit> {
        store.get(BarCode("test", "test"))
    }

    companion object {
        private val EXCEPTION = RuntimeException()
    }
}
