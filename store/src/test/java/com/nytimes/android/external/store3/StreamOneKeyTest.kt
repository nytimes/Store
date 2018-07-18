package com.nytimes.android.external.store3

import com.nytimes.android.external.store3.base.Fetcher
import com.nytimes.android.external.store3.base.Persister
import com.nytimes.android.external.store3.base.impl.BarCode
import com.nytimes.android.external.store3.base.impl.Store
import com.nytimes.android.external.store3.base.impl.StoreBuilder
import io.reactivex.Maybe
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.runners.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class StreamOneKeyTest {

    @Mock
    internal lateinit var fetcher: Fetcher<String, BarCode>
    @Mock
    internal lateinit var persister: Persister<String, BarCode>

    private val barCode = BarCode("key", "value")
    private val barCode2 = BarCode("key2", "value2")
    private lateinit var store: Store<String, BarCode>

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        store = StoreBuilder.barcode<String>()
                .persister(persister)
                .fetcher(fetcher)
                .open()

        `when`(fetcher.fetch(barCode))
                .thenReturn(Single.just(TEST_ITEM))
                .thenReturn(Single.just(TEST_ITEM2))

        `when`(persister.read(barCode))
                .thenReturn(Maybe.empty())
                .thenReturn(Maybe.just(TEST_ITEM))
                .thenReturn(Maybe.just(TEST_ITEM2))

        `when`(persister.write(barCode, TEST_ITEM))
                .thenReturn(Single.just(true))
        `when`(persister.write(barCode, TEST_ITEM2))
                .thenReturn(Single.just(true))
    }

    @Test
    fun testStream() {
        val streamObservable = store.stream(barCode).test()
        //first time we subscribe to stream it will fail getting from memory & disk and instead
        //fetch from network, write to disk and notifiy subscribers
        streamObservable.assertValueCount(1)

        store.clear()
        //fetch should notify subscribers again
        store.fetch(barCode).test().awaitCount(1)
        streamObservable.assertValues(TEST_ITEM, TEST_ITEM2)

        //get for another barcode should not trigger a stream for barcode1
        `when`(fetcher.fetch(barCode2))
                .thenReturn(Single.just(TEST_ITEM))
        `when`(persister.read(barCode2))
                .thenReturn(Maybe.empty())
                .thenReturn(Maybe.just(TEST_ITEM))
        `when`(persister.write(barCode2, TEST_ITEM))
                .thenReturn(Single.just(true))
        store.get(barCode2).test().awaitCount(1)
        streamObservable.assertValueCount(2)
    }

    companion object {
        private const val TEST_ITEM = "test"
        private const val TEST_ITEM2 = "test2"
    }
}
