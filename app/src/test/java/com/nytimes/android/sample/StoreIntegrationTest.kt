package com.nytimes.android.sample

import com.nytimes.android.external.store3.base.Fetcher
import com.nytimes.android.external.store3.base.impl.BarCode
import com.nytimes.android.external.store3.base.impl.Store
import com.nytimes.android.external.store3.base.impl.StoreBuilder
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger

class StoreIntegrationTest {

    lateinit var store: Store<String, BarCode>
    val atomicInt = AtomicInteger(0)
    @Before
    @Throws(Exception::class)
    fun setUp() {
        store = StoreBuilder.barcode<String>()
                .fetcher(fetcher = object : Fetcher<String, BarCode> {
                    override suspend fun fetch(key: BarCode): String {
                        return atomicInt.incrementAndGet().toString()
                    }
                })
                .open()
    }

    @Test
    @Throws(Exception::class)
    fun testRepeatedGet() {
        val barcode = BarCode.empty()
        runBlocking {
            val first = store.get(barcode)
            val same = store.fresh(barcode)
            assertEquals(first, same)
        }
    }


}
