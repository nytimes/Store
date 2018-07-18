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
class StreamTest {

    @Mock
    internal lateinit var fetcher: Fetcher<String, BarCode>
    @Mock
    internal lateinit var persister: Persister<String, BarCode>

    private val barCode = BarCode("key", "value")
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

        `when`(persister.read(barCode))
                .thenReturn(Maybe.empty())
                .thenReturn(Maybe.just(TEST_ITEM))

        `when`(persister.write(barCode, TEST_ITEM))
                .thenReturn(Single.just(true))
    }

    @Test
    fun testStream() {
        val streamObservable = store.stream().test()
        streamObservable.assertValueCount(0)
        store.get(barCode).subscribe()
        streamObservable.assertValueCount(1)
    }

    @Test
    fun testStreamEmitsOnlyFreshData() {
        store.get(barCode).subscribe()
        val streamObservable = store.stream().test()
        streamObservable.assertValueCount(0)
    }

    companion object {
        private const val TEST_ITEM = "test"
    }
}
