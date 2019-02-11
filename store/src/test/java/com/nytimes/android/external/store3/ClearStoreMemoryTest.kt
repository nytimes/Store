package com.nytimes.android.external.store3

import com.nytimes.android.external.store3.base.impl.BarCode
import com.nytimes.android.external.store3.base.impl.Store
import com.nytimes.android.external.store3.base.impl.StoreBuilder

import org.junit.Before
import org.junit.Test

import io.reactivex.Single

import org.assertj.core.api.Assertions.assertThat

class ClearStoreMemoryTest {

    private var networkCalls = 0
    private lateinit var store: Store<Int, BarCode>

    @Before
    fun setUp() {
        networkCalls = 0
        store = StoreBuilder.barcode<Int>()
                .fetcher { barCode -> Single.fromCallable { networkCalls++ } }
                .open()
    }

    @Test
    fun testClearSingleBarCode() {
        //one request should produce one call
        val barcode = BarCode("type", "key")
        store.get(barcode).test().awaitTerminalEvent()
        assertThat(networkCalls).isEqualTo(1)

        // after clearing the memory another call should be made
        store.clearMemory(barcode)
        store.get(barcode).test().awaitTerminalEvent()
        assertThat(networkCalls).isEqualTo(2)
    }

    @Test
    fun testClearAllBarCodes() {
        val b1 = BarCode("type1", "key1")
        val b2 = BarCode("type2", "key2")

        //each request should produce one call
        store.get(b1).test().awaitTerminalEvent()
        store.get(b2).test().awaitTerminalEvent()
        assertThat(networkCalls).isEqualTo(2)

        store.clearMemory()

        //after everything is cleared each request should produce another 2 calls
        store.get(b1).test().awaitTerminalEvent()
        store.get(b2).test().awaitTerminalEvent()
        assertThat(networkCalls).isEqualTo(4)
    }
}
