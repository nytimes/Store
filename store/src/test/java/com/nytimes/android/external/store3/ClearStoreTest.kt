package com.nytimes.android.external.store3

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.nytimes.android.external.store3.base.impl.BarCode
import com.nytimes.android.external.store3.base.impl.Store
import com.nytimes.android.external.store3.base.impl.StoreBuilder
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.verify
import java.util.concurrent.atomic.AtomicInteger

class ClearStoreTest {
    val persister: ClearingPersister = mock()
    private lateinit var networkCalls: AtomicInteger
    private lateinit var store: Store<Int, BarCode>

    @Before
    fun setUp() {
        networkCalls = AtomicInteger(0)
        store = StoreBuilder.barcode<Int>()
                .fetcher { networkCalls.incrementAndGet() }
                .persister(persister)
                .open()
    }

    @Test
    fun testClearSingleBarCode() = runBlocking<Unit> {
        // one request should produce one call
        val barcode = BarCode("type", "key")

        whenever(persister.read(barcode))
                .thenReturn(null) //read from disk on get
                .thenReturn(1) //read from disk after fetching from network
                .thenReturn(null) //read from disk after clearing
                .thenReturn(1) //read from disk after making additional network call
        whenever(persister.write(barcode, 1)).thenReturn(true)
        whenever(persister.write(barcode, 2)).thenReturn(true)

        store.get(barcode)
        assertThat(networkCalls.toInt()).isEqualTo(1)

        // after clearing the memory another call should be made
        store.clear(barcode)
        store.get(barcode)
        verify<ClearingPersister>(persister).clear(barcode)
        assertThat(networkCalls.toInt()).isEqualTo(2)
    }

    @Test
    fun testClearAllBarCodes() = runBlocking<Unit> {
        val barcode1 = BarCode("type1", "key1")
        val barcode2 = BarCode("type2", "key2")

        whenever(persister.read(barcode1))
                .thenReturn(null) //read from disk
                .thenReturn(1) //read from disk after fetching from network
                .thenReturn(null) //read from disk after clearing disk cache
                .thenReturn(1) //read from disk after making additional network call
        whenever(persister.write(barcode1, 1)).thenReturn(true)
        whenever(persister.write(barcode1, 2)).thenReturn(true)

        whenever(persister.read(barcode2))
                .thenReturn(null) //read from disk
                .thenReturn(1) //read from disk after fetching from network
                .thenReturn(null) //read from disk after clearing disk cache
                .thenReturn(1) //read from disk after making additional network call

        whenever(persister.write(barcode2, 1)).thenReturn(true)
        whenever(persister.write(barcode2, 2)).thenReturn(true)

        // each request should produce one call
        store.get(barcode1)
        store.get(barcode2)
        assertThat(networkCalls.toInt()).isEqualTo(2)

        store.clearMemory()

        // after everything is cleared each request should produce another 2 calls
        store.get(barcode1)
        store.get(barcode2)
        assertThat(networkCalls.toInt()).isEqualTo(4)
    }
}
