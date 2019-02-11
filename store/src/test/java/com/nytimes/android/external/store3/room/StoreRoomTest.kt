package com.nytimes.android.external.store3.room

import com.nhaarman.mockitokotlin2.mock
import com.nytimes.android.external.store3.base.impl.BarCode
import com.nytimes.android.external.store3.base.impl.StalePolicy
import com.nytimes.android.external.store3.base.impl.room.StoreRoom
import com.nytimes.android.external.store3.base.room.RoomFetcher
import com.nytimes.android.external.store3.base.room.RoomPersister
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.mockito.Mockito.*
import java.util.concurrent.atomic.AtomicInteger

class StoreRoomTest {
    val counter = AtomicInteger(0)
    val fetcher: RoomFetcher<String, BarCode> = mock()
    val persister: RoomPersister<String, String, BarCode> = mock()
    private val barCode = BarCode("key", "value")

    @Test
    fun testSimple() {

        val simpleStore = StoreRoom.from(
                fetcher,
                persister,
                StalePolicy.UNSPECIFIED
        )


        `when`(fetcher.fetch(barCode))
                .thenReturn(Observable.just(NETWORK))

        `when`(persister.read(barCode))
                .thenReturn(Observable.empty())
                .thenReturn(Observable.just(DISK))


        var value = simpleStore.get(barCode).blockingFirst()

        assertThat(value).isEqualTo(DISK)
        value = simpleStore.get(barCode).blockingFirst()
        assertThat(value).isEqualTo(DISK)
        verify(fetcher, times(1)).fetch(barCode)
    }


    @Test
    fun testDoubleTap() {
        val simpleStore = StoreRoom.from(
                fetcher,
                persister,
                StalePolicy.UNSPECIFIED
        )

        val networkSingle = Single.create<String> { emitter ->
            if (counter.incrementAndGet() == 1) {
                emitter.onSuccess(NETWORK)
            } else {
                emitter.onError(RuntimeException("Yo Dawg your inflight is broken"))
            }
        }

        `when`(fetcher.fetch(barCode))
                .thenReturn(networkSingle.toObservable())

        `when`(persister.read(barCode))
                .thenReturn(Observable.empty())
                .thenReturn(Observable.just(DISK))


        val response = simpleStore.get(barCode)
                .zipWith(simpleStore.get(barCode), BiFunction<String, String, String> { s, s2 -> "hello" })
                .blockingFirst()
        assertThat(response).isEqualTo("hello")
        verify(fetcher, times(1)).fetch(barCode)
    }

    companion object {

        private val DISK = "disk"
        private val NETWORK = "fresh"
    }
}
