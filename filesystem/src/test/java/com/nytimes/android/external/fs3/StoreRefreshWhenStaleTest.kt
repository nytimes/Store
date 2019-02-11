package com.nytimes.android.external.fs3

import com.nytimes.android.external.store3.base.Fetcher
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
import io.reactivex.observers.TestObserver
import okio.BufferedSource

import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

@RunWith(MockitoJUnitRunner::class)
class StoreRefreshWhenStaleTest {
    @Mock
    internal lateinit var fetcher: Fetcher<BufferedSource, BarCode>
    @Mock
    internal lateinit var persister: RecordPersister
    @Mock
    internal lateinit var network1: BufferedSource
    @Mock
    internal lateinit var disk1: BufferedSource
    @Mock
    internal lateinit var disk2: BufferedSource

    private val barCode = BarCode("key", "value")
    private lateinit var store: Store<BufferedSource, BarCode>

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        store = StoreBuilder.barcode<BufferedSource>()
                .fetcher(fetcher)
                .persister(persister)
                .refreshOnStale()
                .open()
    }

    @Test
    fun diskWasRefreshedWhenStaleRecord() {
        `when`(fetcher.fetch(barCode))
                .thenReturn(Single.just(network1))
        `when`(persister.read(barCode))
                .thenReturn(Maybe.just(disk1))  //get should return from disk
        `when`(persister.getRecordState(barCode)).thenReturn(RecordState.STALE)

        `when`(persister.write(barCode, network1))
                .thenReturn(Single.just(true))

        store.get(barCode).test().awaitTerminalEvent()
        verify<Fetcher<BufferedSource, BarCode>>(fetcher, times(1)).fetch(barCode)
        verify<RecordPersister>(persister, times(2)).getRecordState(barCode)
        verify<RecordPersister>(persister, times(1)).write(barCode, network1)
        verify<RecordPersister>(persister, times(2)).read(barCode) //reads from disk a second time when backfilling
    }

    @Test
    fun diskWasNotRefreshedWhenFreshRecord() {
        `when`(fetcher.fetch(barCode))
                .thenReturn(Single.just(network1))
        `when`(persister.read(barCode))
                .thenReturn(Maybe.just(disk1))  //get should return from disk
                .thenReturn(Maybe.just(disk2)) //backfill should read from disk again
        `when`(persister.getRecordState(barCode)).thenReturn(RecordState.FRESH)

        `when`(persister.write(barCode, network1))
                .thenReturn(Single.just(true))

        var testObserver: TestObserver<BufferedSource> = store
                .get(barCode)
                .test()
        testObserver.awaitTerminalEvent()
        testObserver.assertNoErrors()
        testObserver.assertResult(disk1)
        verify<Fetcher<BufferedSource, BarCode>>(fetcher, times(0)).fetch(barCode)
        verify<RecordPersister>(persister, times(1)).getRecordState(barCode)

        store.clear(barCode)
        testObserver = store
                .get(barCode)
                .test()
        testObserver.awaitTerminalEvent()
        testObserver.assertResult(disk2)
        verify<Fetcher<BufferedSource, BarCode>>(fetcher, times(0)).fetch(barCode)
        verify<RecordPersister>(persister, times(2)).getRecordState(barCode)
    }
}
