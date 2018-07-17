package com.nytimes.android.external.fs3

import com.nytimes.android.external.store3.base.Fetcher
import com.nytimes.android.external.store3.base.Persister
import com.nytimes.android.external.store3.base.RecordProvider
import com.nytimes.android.external.store3.base.RecordState
import com.nytimes.android.external.store3.base.impl.BarCode
import com.nytimes.android.external.store3.base.impl.Store
import com.nytimes.android.external.store3.base.impl.StoreBuilder

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.runners.MockitoJUnitRunner

import io.reactivex.Maybe
import io.reactivex.Single
import okio.BufferedSource

import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

@RunWith(MockitoJUnitRunner::class)
class StoreNetworkBeforeStaleFailTest {
    @Mock
    lateinit var fetcher: Fetcher<BufferedSource, BarCode>
    private lateinit var store: Store<BufferedSource, BarCode>

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        store = StoreBuilder.barcode<BufferedSource>()
                .fetcher(fetcher)
                .persister(TestPersister())
                .networkBeforeStale()
                .open()
    }

    @Test
    fun networkBeforeStaleNoNetworkResponse() {
        val exception = Single.error<BufferedSource>(SORRY)
        `when`(fetcher.fetch(barCode))
                .thenReturn(exception)
        store.get(barCode).test().assertError(SORRY)
        verify<Fetcher<BufferedSource, BarCode>>(fetcher, times(1)).fetch(barCode)
    }

    private class TestPersister : Persister<BufferedSource, BarCode>, RecordProvider<BarCode> {
        override fun getRecordState(barCode: BarCode): RecordState {
            return RecordState.MISSING
        }

        override fun read(barCode: BarCode): Maybe<BufferedSource> {
            return Maybe.error(SORRY)
        }

        override fun write(barCode: BarCode,
                           bufferedSource: BufferedSource): Single<Boolean> {
            return Single.just(true)
        }
    }

    companion object {
        private val SORRY = Exception("sorry")
        private val barCode = BarCode("key", "value")
    }
}
