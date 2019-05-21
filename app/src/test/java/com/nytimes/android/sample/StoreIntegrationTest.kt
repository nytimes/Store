package com.nytimes.android.sample

import com.nytimes.android.external.store3.base.impl.BarCode
import com.nytimes.android.external.store3.base.impl.Store
import com.nytimes.android.external.store3.base.impl.StoreBuilder
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger

class StoreIntegrationTest {

    private val atomicInt = AtomicInteger(0)

    private val store: Store<String, BarCode> = StoreBuilder.barcode<String>()
            .fetcher { atomicInt.incrementAndGet().toString() }
            .open()

    @Test
    fun testRepeatedGet() {
        val barcode = BarCode.empty()
        runBlocking {
            val first = store.fresh(barcode)
            val same = store.get(barcode)
            assertEquals(first, same)
        }
    }
}
