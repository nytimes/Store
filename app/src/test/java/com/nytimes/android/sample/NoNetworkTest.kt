package com.nytimes.android.sample

import com.nytimes.android.external.store3.base.Fetcher
import com.nytimes.android.external.store3.base.impl.BarCode
import com.nytimes.android.external.store3.base.impl.Store
import com.nytimes.android.external.store3.base.impl.StoreBuilder

import org.junit.Before
import org.junit.Test

import io.reactivex.Single
import kotlinx.coroutines.runBlocking

class NoNetworkTest {
    private lateinit var store: Store<Any, BarCode>

    @Before
    fun setUp() {
        store = StoreBuilder.barcode<Any>()
                .fetcher(object :Fetcher<Any, BarCode>{
                    override suspend fun fetch(key: BarCode): Any {
                        throw EXCEPTION

                    }
                })
                .open()

    }

    @Test(expected = java.lang.Exception::class)
    @Throws(Exception::class)
    fun testNoNetwork() {
        runBlocking {
            store.get(BarCode("test", "test"))
        }


    }
    
    companion object {
        private val EXCEPTION = RuntimeException()
    }
}
