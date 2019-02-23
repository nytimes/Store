package com.nytimes.android.external.fs3

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.nytimes.android.external.store3.base.Fetcher
import com.nytimes.android.external.store3.base.RecordState
import com.nytimes.android.external.store3.base.impl.BarCode
import com.nytimes.android.external.store3.base.impl.StoreBuilder
import kotlinx.coroutines.runBlocking
import okio.BufferedSource
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.mockito.Mockito.times
import org.mockito.Mockito.verify

class StoreRefreshWhenStaleTest {
    private val fetcher: Fetcher<BufferedSource, BarCode> = mock()
    private val persister: RecordPersister = mock()
    private val network1: BufferedSource = mock()
    private val disk1: BufferedSource = mock()
    private val disk2: BufferedSource = mock()

    private val barCode = BarCode("key", "value")
    private val store = StoreBuilder.barcode<BufferedSource>()
            .fetcher(fetcher)
            .persister(persister)
            .refreshOnStale()
            .open()

    @Test
    fun diskWasRefreshedWhenStaleRecord() = runBlocking<Unit> {
        whenever(fetcher.fetch(barCode))
                .thenReturn(network1)
        whenever(persister.read(barCode))
                .thenReturn(disk1)  //get should return from disk
        whenever(persister.getRecordState(barCode)).thenReturn(RecordState.STALE)

        whenever(persister.write(barCode, network1))
                .thenReturn(true)

        store.get(barCode)
        verify(fetcher, times(1)).fetch(barCode)
        verify(persister, times(2)).getRecordState(barCode)
        verify(persister, times(1)).write(barCode, network1)
        verify(persister, times(2)).read(barCode) //reads from disk a second time when backfilling
    }

    @Test
    fun diskWasNotRefreshedWhenFreshRecord() = runBlocking<Unit> {
        whenever(fetcher.fetch(barCode))
                .thenReturn(network1)
        whenever(persister.read(barCode))
                .thenReturn(disk1)  //get should return from disk
                .thenReturn(disk2) //backfill should read from disk again
        whenever(persister.getRecordState(barCode)).thenReturn(RecordState.FRESH)

        whenever(persister.write(barCode, network1))
                .thenReturn(true)

        assertThat(store.get(barCode)).isEqualTo(disk1)

        verify(fetcher, times(0)).fetch(barCode)
        verify(persister, times(1)).getRecordState(barCode)

        store.clear(barCode)
        assertThat(store.get(barCode)).isEqualTo(disk2)
        verify(fetcher, times(0)).fetch(barCode)
        verify(persister, times(2)).getRecordState(barCode)
    }
}
