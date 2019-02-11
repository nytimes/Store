package com.nytimes.android.sample

import com.nytimes.android.external.store3.base.Fetcher
import com.nytimes.android.external.store3.base.impl.BarCode
import com.nytimes.android.external.store3.base.impl.Store
import com.nytimes.android.external.store3.base.impl.StoreBuilder
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger

class ClearStoreMemoryTest {

    lateinit var networkCalls: AtomicInteger
    lateinit var store: Store<Int, BarCode>

    @Before
    fun setUp() {
        networkCalls = AtomicInteger(0)
        store = StoreBuilder.barcode<Int>()
                .fetcher(object : Fetcher<Int, BarCode> {
                    override suspend fun fetch(key: BarCode) = networkCalls.incrementAndGet()

                })
                .open()
    }

    @Test
    fun testClearSingleBarCode() {
        runBlocking{
            //one request should produce one call
            val barcode = BarCode("type", "key")
            val result = store.get(barcode)
            assertThat(networkCalls.get()).isEqualTo(1)

            store.clearMemory(barcode)
            val anotherResult = store.get(barcode)
            assertThat(networkCalls.get()).isEqualTo(2)
        }
    }

    @Test
    fun testClearAllBarCodes() {
        runBlocking {

            val b1 = BarCode("type1", "key1")
            val b2 = BarCode("type2", "key2")

            //each request should produce one call
            store.get(b1)
            store.get(b2)
            assertThat(networkCalls.get()).isEqualTo(2)

            store.clearMemory()

            //after everything is cleared each request should produce another 2 calls
            store.get(b1)
            store.get(b2)
            assertThat(networkCalls.get()).isEqualTo(4)
        }
    }
}
