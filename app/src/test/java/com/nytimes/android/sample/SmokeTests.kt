package com.nytimes.android.sample

import com.nytimes.android.external.cache3.CacheBuilder
import com.nytimes.android.external.store3.base.Fetcher
import com.nytimes.android.external.store3.base.Persister
import com.nytimes.android.external.store3.base.impl.BarCode
import com.nytimes.android.external.store3.base.impl.StoreBuilder
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class SmokeTests {
    lateinit var counter: AtomicInteger
    lateinit var fetcher: Fetcher<String, BarCode>
    lateinit var persister: Persister<String, BarCode>
    private val barCode = BarCode("key", "value")

    @Before
    fun setUp() {
        counter = AtomicInteger(0)
    }

    @Test
    fun testSimple() {

        fetcher = object : Fetcher<String, BarCode> {
            override suspend fun fetch(key: BarCode) = counter.incrementAndGet().toString()
        }


        persister = object : Persister<String, BarCode> {
            override suspend fun read(key: BarCode): String? {
                if (counter.get() == 0) return null
                else if (counter.get() == 1) return DISK
                else return "WRONG VALUE"
            }

            override suspend fun write(key: BarCode, raw: String): Boolean {
                return true
            }
        }

        val simpleStore = StoreBuilder.barcode<String>()
                .persister(persister)
                .fetcher(fetcher!!)
                .open()

        runBlocking {
            var value = simpleStore.get(barCode)
            assertThat(value).isEqualTo(DISK)
            value = simpleStore.get(barCode)
            assertThat(value).isEqualTo(DISK)
            assertThat(counter.get()).isEqualTo(1)
        }
    }


    @Test
    fun testDoubleTap() {

        fetcher = object : Fetcher<String, BarCode> {
            override suspend fun fetch(key: BarCode) = counter.incrementAndGet().toString()
        }

        persister = object : Persister<String, BarCode> {
            override suspend fun read(key: BarCode): String? {
                if (counter.get() == 0) return null
                else if (counter.get() == 1) return DISK
                else return "WRONG VALUE"
            }

            override suspend fun write(key: BarCode, raw: String): Boolean {
                if (raw != "1") throw RuntimeException("Yo Dawg your inflight is broken")
                return true
            }
        }

        val store = StoreBuilder.barcode<String>()
                .persister(persister)
                .fetcher(fetcher!!)
                .open()


        runBlocking {
            val value: Deferred<String> = async { store.get(barCode) }
            val value2: Deferred<String> = async { store.get(barCode) }
            val result = value.await()
            val result2 = value2.await()
            value2.join()
            assertThat(result).isEqualTo(DISK)
            assertThat(result2).isEqualTo(DISK)
            assertThat(counter.get()).isEqualTo(1)
        }
    }

    @Test
    fun testLazyDefer() {

        fetcher = object : Fetcher<String, BarCode> {
            override suspend fun fetch(key: BarCode) = counter.incrementAndGet().toString()
        }

        persister = object : Persister<String, BarCode> {
            override suspend fun read(key: BarCode): String? {
                if (counter.get() == 0) return null
                else if (counter.get() == 1) return DISK
                else return "WRONG VALUE"
            }

            override suspend fun write(key: BarCode, raw: String): Boolean {
                if (raw != "1") throw RuntimeException("Yo Dawg your inflight is broken")
                return true
            }
        }

        val store = StoreBuilder.barcode<String>()
                .persister(persister)
                .fetcher(fetcher)
                .open()


        runBlocking {
            //same request same suspend function should throttle
            val value: Deferred<String> = async(start = CoroutineStart.LAZY) { store.get(barCode) }
            val value2: Deferred<String> = async(start = CoroutineStart.LAZY) { store.get(barCode) }
            val throwaway: Deferred<String> = async(start = CoroutineStart.LAZY) { store.get(BarCode("m", "n")) }

            value.start()
            value2.start()
            //another request

            val result = value.await()
            val result2 = value2.await()
            assertThat(result).isEqualTo(DISK)
            assertThat(result2).isEqualTo(DISK)
            assertThat(counter.get()).isEqualTo(1)
            throwaway.cancel()
        }
    }

    @Test
    fun testMultiBarcode() {
        val first = BarCode("a", "a")
        val second = BarCode("b", "b")
        fetcher = object : Fetcher<String, BarCode> {
            override suspend fun fetch(key: BarCode): String {
                return counter.incrementAndGet().toString()
            }
        }

        persister = object : Persister<String, BarCode> {
            override suspend fun read(key: BarCode): String? {
                when {
                    counter.get() >= 1 && key == first -> return "first"
                    counter.get() >= 2 && key == second -> return "second"
                    else -> return null
                }
            }

            override suspend fun write(key: BarCode, raw: String): Boolean {
                if (raw != "1" && raw != "2") throw RuntimeException("Yo Dawg your inflight is broken")
                return true
            }
        }

        val store = StoreBuilder.barcode<String>()
                .persister(persister)
                .fetcher(fetcher)
                .open()



        runBlocking {
            val value: Deferred<String> = async(start = CoroutineStart.LAZY) { store.get(first) }
            val value2: Deferred<String> = async(start = CoroutineStart.LAZY) { store.get(second) }

            value.start()
            value2.start()
            //another request

            val result = value.await()
            val result2 = value2.await()
            assertThat(result).isEqualTo("first")
            assertThat(result2).isEqualTo("second")
            assertThat(counter.get()).isEqualTo(2)
        }
    }

    @Test
    fun testEquivalence() {
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

        val DISK = "disk"
        private val NETWORK = "fresh"
        private val MEMORY = "memory"
    }
}
