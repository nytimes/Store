package com.nytimes.android.external.store3

import com.nhaarman.mockitokotlin2.mock
import com.nytimes.android.external.store3.base.Fetcher
import com.nytimes.android.external.store3.base.Persister
import com.nytimes.android.external.store3.base.impl.BarCode
import com.nytimes.android.external.store3.base.impl.Store
import com.nytimes.android.external.store3.base.impl.StoreBuilder
import io.reactivex.Maybe
import io.reactivex.Single
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`

class StreamOneKeyTest {

    val fetcher: Fetcher<String, BarCode> = mock()
    val persister: Persister<String, BarCode> = mock()

    private val barCode = BarCode("key", "value")
    private val barCode2 = BarCode("key2", "value2")

    private val store: Store<String, BarCode> = StoreBuilder.barcode<String>()
            .persister(persister)
            .fetcher(fetcher)
            .open()

    @Before
    fun setUp() = runBlocking<Unit> {
        `when`<Any>(fetcher.fetch(barCode))
                .thenReturn(Single.just(TEST_ITEM))
                .thenReturn(Single.just(TEST_ITEM2))

        `when`<Any>(persister.read(barCode))
                .thenReturn(Maybe.empty<String>())
                .thenReturn(Maybe.just(TEST_ITEM))
                .thenReturn(Maybe.just(TEST_ITEM2))

        `when`<Any>(persister.write(barCode, TEST_ITEM))
                .thenReturn(Single.just(true))
        `when`<Any>(persister.write(barCode, TEST_ITEM2))
                .thenReturn(Single.just(true))
    }

    @Test
    fun testStream() = runBlocking<Unit> {
        val streamObservable = store.stream(barCode).test()
        //first time we subscribe to stream it will fail getting from memory & disk and instead
        //fresh from network, write to disk and notifiy subscribers
        streamObservable.assertValueCount(1)

        store.clear()
        //fresh should notify subscribers again
        store.fresh(barCode)
        streamObservable.assertValues(TEST_ITEM, TEST_ITEM2)

        //get for another barcode should not trigger a stream for barcode1
        `when`<Any>(fetcher.fetch(barCode2))
                .thenReturn(Single.just(TEST_ITEM))
        `when`<Any>(persister.read(barCode2))
                .thenReturn(Maybe.empty<Any>())
                .thenReturn(Maybe.just(TEST_ITEM))
        `when`<Any>(persister.write(barCode2, TEST_ITEM))
                .thenReturn(Single.just(true))
        store.get(barCode2)
        streamObservable.assertValueCount(2)
    }

    companion object {

        private val TEST_ITEM = "test"
        private val TEST_ITEM2 = "test2"
    }
}
