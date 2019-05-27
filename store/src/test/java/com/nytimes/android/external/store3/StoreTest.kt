package com.nytimes.android.external.store3

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.nytimes.android.external.cache3.CacheBuilder
import com.nytimes.android.external.store3.base.Fetcher
import com.nytimes.android.external.store3.base.Persister
import com.nytimes.android.external.store3.base.impl.BarCode
import com.nytimes.android.external.store3.base.impl.StoreBuilder
import com.nytimes.android.external.store3.util.NoopPersister
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.mockito.Mockito.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class StoreTest {
    val counter = AtomicInteger(0)
    val fetcher: Fetcher<String, BarCode> = mock()
    var persister: Persister<String, BarCode> = mock()
    private val barCode = BarCode("key", "value")

    @Test
    fun testSimple() = runBlocking<Unit> {

        val simpleStore = StoreBuilder.barcode<String>()
                .persister(persister)
                .fetcher(fetcher)
                .open()


        whenever(fetcher.fetch(barCode))
                .thenReturn(NETWORK)

        whenever(persister.read(barCode))
                .thenReturn(null)
                .thenReturn(DISK)

        whenever(persister.write(barCode, NETWORK))
                .thenReturn(true)

        var value = simpleStore.get(barCode)

        assertThat(value).isEqualTo(DISK)
        value = simpleStore.get(barCode)
        assertThat(value).isEqualTo(DISK)
        verify(fetcher, times(1)).fetch(barCode)
    }

    @Test
    fun testDoubleTap() = runBlocking<Unit> {

        val simpleStore = StoreBuilder.barcode<String>()
                .persister(persister)
                .fetcher(fetcher)
                .open()

        whenever(fetcher.fetch(barCode))
                .thenAnswer {
                    if (counter.incrementAndGet() == 1) {
                        NETWORK
                    } else {
                        throw RuntimeException("Yo Dawg your inflight is broken")
                    }
                }

        whenever(persister.read(barCode))
                .thenReturn(null)
                .thenReturn(DISK)

        whenever(persister.write(barCode, NETWORK))
                .thenReturn(true)


        val deferred = async { simpleStore.get(barCode) }
        simpleStore.get(barCode)
        deferred.await()

        verify(fetcher, times(1)).fetch(barCode)
    }

    @Test
    fun testSubclass() = runBlocking<Unit> {

        val simpleStore = SampleStore(fetcher, persister)
        simpleStore.clear()

        whenever(fetcher.fetch(barCode))
                .thenReturn(NETWORK)

        whenever(persister.read(barCode))
                .thenReturn(null)
                .thenReturn(DISK)
        whenever(persister.write(barCode, NETWORK)).thenReturn(true)

        var value = simpleStore.get(barCode)
        assertThat(value).isEqualTo(DISK)
        value = simpleStore.get(barCode)
        assertThat(value).isEqualTo(DISK)
        verify(fetcher, times(1)).fetch(barCode)
    }

    @Test
    fun testNoopAndDefault() = runBlocking<Unit> {

        val persister = spy(NoopPersister.create<String, BarCode>())
        val simpleStore = SampleStore(fetcher, persister)


        whenever(fetcher.fetch(barCode))
                .thenReturn(NETWORK)

        var value = simpleStore.get(barCode)
        verify(fetcher, times(1)).fetch(barCode)
        verify(persister, times(1)).write(barCode, NETWORK)
        verify(persister, times(2)).read(barCode)
        assertThat(value).isEqualTo(NETWORK)


        value = simpleStore.get(barCode)
        verify(persister, times(2)).read(barCode)
        verify(persister, times(1)).write(barCode, NETWORK)
        verify(fetcher, times(1)).fetch(barCode)

        assertThat(value).isEqualTo(NETWORK)
    }

    @Test
    fun testEquivalence() = runBlocking<Unit> {
        val cache = CacheBuilder.newBuilder()
                .maximumSize(1)
                .expireAfterAccess(java.lang.Long.MAX_VALUE, TimeUnit.SECONDS)
                .build<BarCode, String>()

        cache.put(barCode, MEMORY)
        var value = cache.getIfPresent(barCode)
        assertThat(value).isEqualTo(MEMORY)

        value = cache.getIfPresent(BarCode(barCode.type, barCode.key))
        assertThat(value).isEqualTo(MEMORY)
    }

    companion object {

        private val DISK = "disk"
        private val NETWORK = "fresh"
        private val MEMORY = "memory"
    }
}
