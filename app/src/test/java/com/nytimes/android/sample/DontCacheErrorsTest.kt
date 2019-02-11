package com.nytimes.android.sample

import com.nytimes.android.external.store3.base.Fetcher
import com.nytimes.android.external.store3.base.impl.BarCode
import com.nytimes.android.external.store3.base.impl.Store
import com.nytimes.android.external.store3.base.impl.StoreBuilder
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test


class DontCacheErrorsTest {

    internal var shouldThrow: Boolean = false
    lateinit var store: Store<Int, BarCode>

    @Before
    fun setUp() {
        store = StoreBuilder.barcode<Int>()
                .fetcher(object : Fetcher<Int, BarCode> {
                    override suspend fun fetch(key: BarCode): Int {
                        if (shouldThrow) {
                            throw RuntimeException()
                        } else {
                            return 0
                        }
                    }
                })
                .open()
    }

    @Test(expected = java.lang.RuntimeException::class)
    fun testStoreDoesntCacheErrors() {
        runBlocking {
            val barcode = BarCode("bar", "code")
            shouldThrow = true
            val job = store.get(barcode)
        }
    }
}
