package com.nytimes.android.external.store3

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.nytimes.android.external.store3.base.Fetcher
import com.nytimes.android.external.store3.base.Persister
import com.nytimes.android.external.store3.base.impl.BarCode
import com.nytimes.android.external.store3.base.impl.Store
import com.nytimes.android.external.store3.base.impl.StoreBuilder
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

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
        whenever(fetcher.fetch(barCode))
                .thenReturn(TEST_ITEM)
                .thenReturn(TEST_ITEM2)

        whenever(persister.read(barCode))
                .thenReturn(TEST_ITEM)
                .thenReturn(TEST_ITEM2)

        whenever(persister.write(barCode, TEST_ITEM))
                .thenReturn(true)
        whenever(persister.write(barCode, TEST_ITEM2))
                .thenReturn(true)
    }

    @Test
    fun testStream() = runBlocking<Unit> {
        val streamSubscription = store.stream(barCode).openChannelSubscription()
        try {//stream doesn't invoke get anymore so when we call it the channel is empty
            assertThat(streamSubscription.isEmpty).isTrue()

            store.clear()
            //fresh should notify subscribers again
            store.fresh(barCode)
            assertThat(streamSubscription.poll()).isEqualTo(TEST_ITEM)
//        assertThat(streamObservable.poll()).isEqualTo(TEST_ITEM2)

            //get for another barcode should not trigger a stream for barcode1
            whenever(fetcher.fetch(barCode2))
                    .thenReturn(TEST_ITEM)
            whenever(persister.read(barCode2))
                    .thenReturn(TEST_ITEM)
            whenever(persister.write(barCode2, TEST_ITEM))
                    .thenReturn(true)
            store.get(barCode2)
            assertThat(streamSubscription.isEmpty).isTrue()
        } finally {
            streamSubscription.cancel()
        }
    }

    companion object {
        private const val TEST_ITEM = "test"
        private const val TEST_ITEM2 = "test2"
    }
}
