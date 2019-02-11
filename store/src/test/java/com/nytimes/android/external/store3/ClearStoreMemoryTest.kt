package com.nytimes.android.external.store3

import com.nytimes.android.external.store3.base.impl.BarCode
import com.nytimes.android.external.store3.base.impl.Store
import com.nytimes.android.external.store3.base.impl.StoreBuilder
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class ClearStoreMemoryTest {

    private var networkCalls = 0
    private lateinit var store: Store<Int, BarCode>

    @Before
    fun setUp() {
        networkCalls = 0
        store = StoreBuilder.barcode<Int>()
                .fetcher { networkCalls++ }
                .open()
    }

    @Test
    fun testClearSingleBarCode() = runBlocking<Unit> {
        //one request should produce one call
        val barcode = BarCode("type", "key")
        store.get(barcode)
        assertThat(networkCalls).isEqualTo(1)

        // after clearing the memory another call should be made
        store.clearMemory(barcode)
        store.get(barcode)
        assertThat(networkCalls).isEqualTo(2)
    }

    @Test
    fun testClearAllBarCodes() = runBlocking<Unit> {
        val b1 = BarCode("type1", "key1")
        val b2 = BarCode("type2", "key2")

        //each request should produce one call
        store.get(b1)
        store.get(b2)
        assertThat(networkCalls).isEqualTo(2)

        store.clearMemory()

        //after everything is cleared each request should produce another 2 calls
        store.get(b1)
        store.get(b2)
        assertThat(networkCalls).isEqualTo(4)
    }
}
