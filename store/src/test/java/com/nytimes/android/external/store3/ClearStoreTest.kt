package com.nytimes.android.external.store3

import com.nytimes.android.external.store3.base.impl.BarCode
import com.nytimes.android.external.store3.base.impl.Store
import com.nytimes.android.external.store3.base.impl.StoreBuilder
import io.reactivex.Maybe
import io.reactivex.Single
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.runners.MockitoJUnitRunner
import java.util.concurrent.atomic.AtomicInteger

@RunWith(MockitoJUnitRunner::class)
class ClearStoreTest {
    @Mock
    lateinit var persister: ClearingPersister
    private lateinit var networkCalls: AtomicInteger
    private lateinit var store: Store<Int, BarCode>

    @Before
    fun setUp() {
        networkCalls = AtomicInteger(0)
        store = StoreBuilder.barcode<Int>()
                .fetcher { barCode -> Single.fromCallable { networkCalls.incrementAndGet() } }
                .persister(persister)
                .open()
    }

    @Test
    fun testClearSingleBarCode() {
        // one request should produce one call
        val barcode = BarCode("type", "key")

        `when`(persister.read(barcode))
                .thenReturn(Maybe.empty()) //read from disk on get
                .thenReturn(Maybe.just(1)) //read from disk after fetching from network
                .thenReturn(Maybe.empty()) //read from disk after clearing
                .thenReturn(Maybe.just(1)) //read from disk after making additional network call
        `when`(persister.write(barcode, 1)).thenReturn(Single.just(true))
        `when`(persister.write(barcode, 2)).thenReturn(Single.just(true))

        store.get(barcode).test().awaitTerminalEvent()
        assertThat(networkCalls.toInt()).isEqualTo(1)

        // after clearing the memory another call should be made
        store.clear(barcode)
        store.get(barcode).test().awaitTerminalEvent()
        verify<ClearingPersister>(persister).clear(barcode)
        assertThat(networkCalls.toInt()).isEqualTo(2)
    }

    @Test
    fun testClearAllBarCodes() {
        val barcode1 = BarCode("type1", "key1")
        val barcode2 = BarCode("type2", "key2")

        `when`(persister.read(barcode1))
                .thenReturn(Maybe.empty()) //read from disk
                .thenReturn(Maybe.just(1)) //read from disk after fetching from network
                .thenReturn(Maybe.empty()) //read from disk after clearing disk cache
                .thenReturn(Maybe.just(1)) //read from disk after making additional network call
        `when`(persister.write(barcode1, 1)).thenReturn(Single.just(true))
        `when`(persister.write(barcode1, 2)).thenReturn(Single.just(true))

        `when`(persister.read(barcode2))
                .thenReturn(Maybe.empty()) //read from disk
                .thenReturn(Maybe.just(1)) //read from disk after fetching from network
                .thenReturn(Maybe.empty()) //read from disk after clearing disk cache
                .thenReturn(Maybe.just(1)) //read from disk after making additional network call

        `when`(persister.write(barcode2, 1)).thenReturn(Single.just(true))
        `when`(persister.write(barcode2, 2)).thenReturn(Single.just(true))

        // each request should produce one call
        store.get(barcode1).test().awaitTerminalEvent()
        store.get(barcode2).test().awaitTerminalEvent()
        assertThat(networkCalls.toInt()).isEqualTo(2)

        store.clear()

        // after everything is cleared each request should produce another 2 calls
        store.get(barcode1).test().awaitTerminalEvent()
        store.get(barcode2).test().awaitTerminalEvent()
        assertThat(networkCalls.toInt()).isEqualTo(4)
    }
}
