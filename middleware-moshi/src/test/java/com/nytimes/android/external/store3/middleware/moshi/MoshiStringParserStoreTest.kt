package com.nytimes.android.external.store3.middleware.moshi

import com.nytimes.android.external.store3.base.Fetcher
import com.nytimes.android.external.store3.base.Persister
import com.nytimes.android.external.store3.base.impl.BarCode
import com.nytimes.android.external.store3.base.impl.ParsingStoreBuilder
import com.squareup.moshi.Moshi
import io.reactivex.Maybe
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class MoshiStringParserStoreTest {
    @Rule
    @JvmField
    var expectedException = ExpectedException.none()
    @Mock
    lateinit var fetcher: Fetcher<String, BarCode>
    @Mock
    lateinit var persister: Persister<String, BarCode>
    private val barCode = BarCode("value", KEY)

    @Before
    @Throws(Exception::class)
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        `when`(fetcher.fetch(barCode))
                .thenReturn(Single.just(source))

        `when`(persister.read(barCode))
                .thenReturn(Maybe.empty())
                .thenReturn(Maybe.just(source))

        `when`(persister.write(barCode, source))
                .thenReturn(Single.just(true))
    }

    @Test
    fun testMoshiString() {
        val store = ParsingStoreBuilder.builder<String, Foo>()
                .persister(persister)
                .fetcher(fetcher)
                .parser(MoshiParserFactory.createStringParser(Foo::class.java))
                .open()

        val result = store.get(barCode).blockingGet()

        assertEquals(result.number.toLong(), 123)
        assertEquals(result.string, "abc")
        assertEquals(result.bars.size.toLong(), 2)
        assertEquals(result.bars[0].string, "def")
        assertEquals(result.bars[1].string, "ghi")

        verify<Fetcher<String, BarCode>>(fetcher, times(1)).fetch(barCode)
    }

    @Test
    fun testNullMoshi() {
        expectedException.expect(NullPointerException::class.java)
        MoshiParserFactory.createStringParser<Any>(null!!, Foo::class.java)
    }

    @Test
    fun testNullType() {
        expectedException.expect(NullPointerException::class.java)
        MoshiParserFactory.createStringParser<Any>(Moshi.Builder().build(), null!!)
    }

    companion object {

        private val KEY = "key"
        private val source = "{\"number\":123,\"string\":\"abc\",\"bars\":[{\"string\":\"def\"},{\"string\":\"ghi\"}]}"
    }

}
