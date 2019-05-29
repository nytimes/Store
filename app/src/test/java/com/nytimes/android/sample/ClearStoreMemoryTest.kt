package com.nytimes.android.sample

import com.nytimes.android.external.store3.base.Fetcher
import com.nytimes.android.external.store3.base.impl.BarCode
import com.nytimes.android.external.store3.base.impl.StoreBuilder
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger

class ClearStoreMemoryTest {

    private val networkCalls = AtomicInteger(0)
    private val store = StoreBuilder.barcode<Int>()
            .fetcher(object : Fetcher<Int, BarCode> {
                override suspend fun fetch(key: BarCode) = networkCalls.incrementAndGet()

            })
            .open()

    @Test
    fun testClearSingleBarCode() = runBlocking<Unit> {
        //one request should produce one call
        val barcode = BarCode("type", "key")
        val result = store.get(barcode)
        assertThat(networkCalls.get()).isEqualTo(1)

        store.clear(barcode)
        val anotherResult = store.get(barcode)
        assertThat(networkCalls.get()).isEqualTo(2)
    }

    @Test
    fun testClearAllBarCodes() = runBlocking<Unit> {
        val b1 = BarCode("type1", "key1")
        val b2 = BarCode("type2", "key2")

        //each request should produce one call
        store.get(b1)
        store.get(b2)
        assertThat(networkCalls.get()).isEqualTo(2)

        store.clear(b1)
        store.clear(b2)

        //after everything is cleared each request should produce another 2 calls
        store.get(b1)
        store.get(b2)
        assertThat(networkCalls.get()).isEqualTo(4)
    }
}
