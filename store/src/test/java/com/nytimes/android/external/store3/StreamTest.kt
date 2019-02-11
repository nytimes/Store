package com.nytimes.android.external.store3

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.nytimes.android.external.store3.base.Fetcher
import com.nytimes.android.external.store3.base.Persister
import com.nytimes.android.external.store3.base.impl.BarCode
import com.nytimes.android.external.store3.base.impl.StoreBuilder
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class StreamTest {

    val fetcher: Fetcher<String, BarCode> = mock()
    val persister: Persister<String, BarCode> = mock()

    private val barCode = BarCode("key", "value")

    private val store = StoreBuilder.barcode<String>()
            .persister(persister)
            .fetcher(fetcher)
            .open()

    @Before
    fun setUp() = runBlocking<Unit> {
        whenever(fetcher.fetch(barCode)).thenReturn(TEST_ITEM)

        whenever(persister.read(barCode))
                .thenReturn(null)
                .thenReturn(TEST_ITEM)

        whenever(persister.write(barCode, TEST_ITEM))
                .thenReturn(true)
    }

    @Test
    fun testStream() = runBlocking<Unit> {
        val streamObservable = store.stream().test()
        streamObservable.assertValueCount(0)
        store.get(barCode)
        streamObservable.assertValueCount(1)
    }

    @Test
    fun testStreamEmitsOnlyFreshData() = runBlocking<Unit> {
        store.get(barCode)
        val streamObservable = store.stream().test()
        streamObservable.assertValueCount(0)
    }

    companion object {

        private val TEST_ITEM = "test"
    }
}
