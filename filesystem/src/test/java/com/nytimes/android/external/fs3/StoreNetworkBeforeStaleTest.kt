package com.nytimes.android.external.fs3

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.nytimes.android.external.store3.base.Fetcher
import com.nytimes.android.external.store3.base.RecordState
import com.nytimes.android.external.store3.base.impl.BarCode
import com.nytimes.android.external.store3.base.impl.StoreBuilder
import junit.framework.Assert.fail
import kotlinx.coroutines.runBlocking
import okio.BufferedSource
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.mockito.Mockito.*
import javax.management.Query.times

class StoreNetworkBeforeStaleTest {

    private val sorry = RuntimeException("sorry")
    private val fetcher: Fetcher<BufferedSource, BarCode> = mock()
    private val persister: RecordPersister = mock()
    private val network1: BufferedSource = mock()
    private val disk1: BufferedSource = mock()

    private val barCode = BarCode("key", "value")
    private val store = StoreBuilder.barcode<BufferedSource>()
            .fetcher(fetcher)
            .persister(persister)
            .networkBeforeStale()
            .open()

    @Test
    fun networkBeforeDiskWhenStale() = runBlocking<Unit> {
        whenever(fetcher.fetch(barCode))
                .thenThrow(RuntimeException())
        whenever(persister.read(barCode))
                .thenReturn(disk1)  //get should return from disk
        whenever(persister.getRecordState(barCode)).thenReturn(RecordState.STALE)

        whenever(persister.write(barCode, network1))
                .thenReturn(true)

        store.get(barCode)

        val inOrder = inOrder(fetcher, persister)
        inOrder.verify(fetcher, times(1)).fetch(barCode)
        inOrder.verify(persister, times(1)).read(barCode)
        verify(persister, never()).write(barCode, network1)
    }

    @Test
    fun noNetworkBeforeStaleWhenMissingRecord() = runBlocking<Unit> {
        whenever(fetcher.fetch(barCode))
                .thenReturn(network1)
        whenever(persister.read(barCode))
                .thenReturn(null, disk1)  //first call should return
        // empty, second call after network should return the network value
        whenever(persister.getRecordState(barCode)).thenReturn(RecordState.MISSING)

        whenever(persister.write(barCode, network1))
                .thenReturn(true)

        store.get(barCode)

        val inOrder = inOrder(fetcher, persister)
        inOrder.verify(persister, times(1)).read(barCode)
        inOrder.verify(fetcher, times(1)).fetch(barCode)
        inOrder.verify(persister, times(1)).write(barCode, network1)
        inOrder.verify(persister, times(1)).read(barCode)
    }

    @Test
    fun noNetworkBeforeStaleWhenFreshRecord() = runBlocking<Unit> {
        whenever(persister.read(barCode))
                .thenReturn(disk1)  //get should return from disk
        whenever(persister.getRecordState(barCode)).thenReturn(RecordState.FRESH)

        store.get(barCode)

        verify(fetcher, never()).fetch(barCode)
        verify(persister, never()).write(barCode, network1)
        verify(persister, times(1)).read(barCode)
    }

    @Test
    fun networkBeforeStaleNoNetworkResponse() = runBlocking<Unit> {
        whenever(fetcher.fetch(barCode))
                .thenThrow(sorry)

        try {
            store.get(barCode)
            fail()
        } catch (e: Exception) {
            assertThat(e.localizedMessage).isEqualTo(sorry.localizedMessage)
        }

        val inOrder = inOrder(fetcher, persister)
        inOrder.verify(persister, times(1)).read(barCode)
        inOrder.verify(fetcher, times(1)).fetch(barCode)
        inOrder.verify(persister, times(1)).read(barCode)
    }
}
