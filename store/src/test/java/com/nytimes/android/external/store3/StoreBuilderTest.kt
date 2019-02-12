package com.nytimes.android.external.store3


import com.nytimes.android.external.store3.base.Persister
import com.nytimes.android.external.store3.base.impl.BarCode
import com.nytimes.android.external.store3.base.impl.StoreBuilder
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.util.*

class StoreBuilderTest {

    @Test
    fun testBuildersBuildWithCorrectTypes() = runBlocking<Unit> {
        //test  is checking whether types are correct in builders
        val store = StoreBuilder.parsedWithKey<Int, String, Date>()
                .fetcher { key -> key.toString() }
                .persister(object : Persister<String, Int> {
                    override suspend fun read(key: Int): String? {
                        return key.toString()
                    }

                    override suspend fun write(key: Int, raw: String) = true
                })
                .parser { DATE }
                .open()


        val barCodeStore = StoreBuilder.barcode<Date>().fetcher { DATE }.open()


        val keyStore = StoreBuilder.key<Int, Date>()
                .fetcher { DATE }
                .open()
        var result = store.get(5)
        result = barCodeStore.get(BarCode("test", "5"))
        result = keyStore.get(5)
        assertThat(result).isNotNull()

    }

    companion object {

        val DATE = Date()
    }
}
