package com.nytimes.android.external.store3.room

import com.nytimes.android.external.store3.base.Clearable
import com.nytimes.android.external.store3.base.impl.BarCode
import com.nytimes.android.external.store3.base.impl.StalePolicy
import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import com.nytimes.android.external.store3.base.room.RoomPersister
import io.reactivex.Observable
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import java.util.concurrent.atomic.AtomicInteger

class ClearStoreRoomTest {
    @Mock
    internal var persister: RoomClearingPersister? = null
    private val networkCalls: AtomicInteger = AtomicInteger(0)
    private var store = StoreRoom.from({ Observable.fromCallable { networkCalls.incrementAndGet() } },
            persister,
            StalePolicy.UNSPECIFIED)

    @Test
    fun testClearSingleBarCode() {
        // one request should produce one call
        val barcode = BarCode("type", "key")

        `when`(persister!!.read(barcode))
                .thenReturn(Observable.empty()) //read from disk on get
                .thenReturn(Observable.just(1)) //read from disk after fetching from network
                .thenReturn(Observable.empty()) //read from disk after clearing
                .thenReturn(Observable.just(1)) //read from disk after making additional network call

        store!!.get(barcode).test().awaitTerminalEvent()
        assertThat(networkCalls.toInt()).isEqualTo(1)

        // after clearing the memory another call should be made
        store!!.clear(barcode)
        store!!.get(barcode).test().awaitTerminalEvent()
        verify<RoomClearingPersister>(persister).clear(barcode)
        assertThat(networkCalls.toInt()).isEqualTo(2)
    }

    @Test
    fun testClearAllBarCodes() {
        val barcode1 = BarCode("type1", "key1")
        val barcode2 = BarCode("type2", "key2")

        `when`(persister!!.read(barcode1))
                .thenReturn(Observable.empty()) //read from disk
                .thenReturn(Observable.just(1)) //read from disk after fetching from network
                .thenReturn(Observable.empty()) //read from disk after clearing disk cache
                .thenReturn(Observable.just(1)) //read from disk after making additional network call

        `when`(persister!!.read(barcode2))
                .thenReturn(Observable.empty()) //read from disk
                .thenReturn(Observable.just(1)) //read from disk after fetching from network
                .thenReturn(Observable.empty()) //read from disk after clearing disk cache
                .thenReturn(Observable.just(1)) //read from disk after making additional network call


        // each request should produce one call
        store!!.get(barcode1).test().awaitTerminalEvent()
        store!!.get(barcode2).test().awaitTerminalEvent()
        assertThat(networkCalls.toInt()).isEqualTo(2)

        store!!.clear()

        // after everything is cleared each request should produce another 2 calls
        store!!.get(barcode1).test().awaitTerminalEvent()
        store!!.get(barcode2).test().awaitTerminalEvent()
        assertThat(networkCalls.toInt()).isEqualTo(4)
    }

    //everything will be mocked
    internal class RoomClearingPersister : RoomPersister<Int, Int, BarCode>, Clearable<BarCode> {
        override fun clear(key: BarCode) {
            throw RuntimeException()
        }

        override fun read(barCode: BarCode): Observable<Int> {
            throw RuntimeException()
        }

        override fun write(barCode: BarCode, integer: Int) {
            //noop
        }
    }
}
