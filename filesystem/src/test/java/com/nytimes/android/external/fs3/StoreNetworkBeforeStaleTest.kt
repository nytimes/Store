package com.nytimes.android.external.fs3

import com.nytimes.android.external.store3.base.Fetcher
import com.nytimes.android.external.store3.base.RecordState
import com.nytimes.android.external.store3.base.impl.BarCode
import com.nytimes.android.external.store3.base.impl.Store
import com.nytimes.android.external.store3.base.impl.StoreBuilder
import io.reactivex.Maybe
import io.reactivex.Single
import okio.BufferedSource
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.runners.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class StoreNetworkBeforeStaleTest {

    private val sorry = Exception("sorry")
    @Mock
    lateinit var fetcher: Fetcher<BufferedSource, BarCode>
    @Mock
    lateinit var persister: RecordPersister
    @Mock
    lateinit var network1: BufferedSource
    @Mock
    lateinit var disk1: BufferedSource

    private val barCode = BarCode("key", "value")
    private lateinit var store: Store<BufferedSource, BarCode>

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        store = StoreBuilder.barcode<BufferedSource>()
                .fetcher(fetcher)
                .persister(persister)
                .networkBeforeStale()
                .open()
    }

    @Test
    fun networkBeforeDiskWhenStale() {
        `when`(fetcher.fetch(barCode))
                .thenReturn(Single.error(Exception()))
        `when`(persister.read(barCode))
                .thenReturn(Maybe.just(disk1))  //get should return from disk
        `when`(persister.getRecordState(barCode)).thenReturn(RecordState.STALE)

        `when`(persister.write(barCode, network1))
                .thenReturn(Single.just(true))

        store.get(barCode).test().awaitTerminalEvent()

        val inOrder = inOrder(fetcher, persister)
        inOrder.verify<Fetcher<BufferedSource, BarCode>>(fetcher, times(1)).fetch(barCode)
        inOrder.verify<RecordPersister>(persister, times(1)).read(barCode)
        verify<RecordPersister>(persister, never()).write(barCode, network1)
    }

    @Test
    fun noNetworkBeforeStaleWhenMissingRecord() {
        `when`(fetcher.fetch(barCode))
                .thenReturn(Single.just(network1))
        `when`(persister.read(barCode))
                .thenReturn(Maybe.empty(), Maybe.just(disk1))  //first call should return
        // empty, second call after network should return the network value
        `when`(persister.getRecordState(barCode)).thenReturn(RecordState.MISSING)

        `when`(persister.write(barCode, network1))
                .thenReturn(Single.just(true))

        store.get(barCode).test().awaitTerminalEvent()

        val inOrder = inOrder(fetcher, persister)
        inOrder.verify<RecordPersister>(persister, times(1)).read(barCode)
        inOrder.verify<Fetcher<BufferedSource, BarCode>>(fetcher, times(1)).fetch(barCode)
        inOrder.verify<RecordPersister>(persister, times(1)).write(barCode, network1)
        inOrder.verify<RecordPersister>(persister, times(1)).read(barCode)
    }

    @Test
    fun noNetworkBeforeStaleWhenFreshRecord() {
        `when`(persister.read(barCode))
                .thenReturn(Maybe.just(disk1))  //get should return from disk
        `when`(persister.getRecordState(barCode)).thenReturn(RecordState.FRESH)

        store.get(barCode).test().awaitTerminalEvent()

        verify<Fetcher<BufferedSource, BarCode>>(fetcher, never()).fetch(barCode)
        verify<RecordPersister>(persister, never()).write(barCode, network1)
        verify<RecordPersister>(persister, times(1)).read(barCode)
    }

    @Test
    fun networkBeforeStaleNoNetworkResponse() {
        val singleError = Single.error<BufferedSource>(sorry)
        val maybeError = Maybe.error<BufferedSource>(sorry)
        `when`(fetcher.fetch(barCode))
                .thenReturn(singleError)
        `when`(persister.read(barCode))
                .thenReturn(maybeError, maybeError)  //first call should return
        // empty, second call after network should return the network value
        `when`(persister.getRecordState(barCode)).thenReturn(RecordState.MISSING)

        `when`(persister.write(barCode, network1))
                .thenReturn(Single.just(true))

        store.get(barCode).test().assertError(sorry)

        val inOrder = inOrder(fetcher, persister)
        inOrder.verify<RecordPersister>(persister, times(1)).read(barCode)
        inOrder.verify<Fetcher<BufferedSource, BarCode>>(fetcher, times(1)).fetch(barCode)
        inOrder.verify<RecordPersister>(persister, times(1)).read(barCode)
    }
}
