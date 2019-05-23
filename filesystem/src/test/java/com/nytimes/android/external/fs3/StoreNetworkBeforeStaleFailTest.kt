package com.nytimes.android.external.fs3

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.nytimes.android.external.store3.base.Fetcher
import com.nytimes.android.external.store3.base.Persister
import com.nytimes.android.external.store3.base.RecordProvider
import com.nytimes.android.external.store3.base.RecordState
import com.nytimes.android.external.store3.base.impl.BarCode
import com.nytimes.android.external.store3.base.impl.StoreBuilder
import kotlinx.coroutines.runBlocking
import okio.BufferedSource
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.Test
import org.mockito.Mockito.times
import org.mockito.Mockito.verify

class StoreNetworkBeforeStaleFailTest {
    private val fetcher: Fetcher<BufferedSource, BarCode> = mock()
    private val store = StoreBuilder.barcode<BufferedSource>()
            .fetcher(fetcher)
            .persister(TestPersister())
            .networkBeforeStale()
            .open()

    @Test
    fun networkBeforeStaleNoNetworkResponse() = runBlocking<Unit> {
        whenever(fetcher.fetch(barCode)).thenThrow(SORRY)
        try {
            store.get(barCode)
            fail("Exception not thrown")
        } catch (e: Exception) {
            assertThat(e.localizedMessage).isEqualTo(SORRY.localizedMessage)
        }
        verify(fetcher, times(1)).fetch(barCode)
    }

    private class TestPersister : Persister<BufferedSource, BarCode>, RecordProvider<BarCode> {
        override fun getRecordState(barCode: BarCode): RecordState {
            return RecordState.MISSING
        }

        override suspend fun read(key: BarCode): BufferedSource? {
            throw SORRY
        }

        override suspend fun write(key: BarCode, raw: BufferedSource) = true
    }

    companion object {
        private val SORRY = RuntimeException("sorry")
        private val barCode = BarCode("key", "value")
    }
}
