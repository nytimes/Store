package com.nytimes.android.external.store3.middleware.moshi

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.nytimes.android.external.store3.base.Fetcher
import com.nytimes.android.external.store3.base.Persister
import com.nytimes.android.external.store3.base.impl.BarCode
import com.nytimes.android.external.store3.base.impl.ParsingStoreBuilder
import com.squareup.moshi.Moshi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.mockito.Mockito.times
import org.mockito.Mockito.verify

class MoshiStringParserStoreTest {
    @Rule
    @JvmField
    var expectedException = ExpectedException.none()
    private val fetcher: Fetcher<String, BarCode> = mock()
    private val persister: Persister<String, BarCode> = mock()
    private val barCode = BarCode("value", KEY)

    @Before
    fun setUp() = runBlocking<Unit> {
        whenever(fetcher.fetch(barCode))
                .thenReturn(source)

        whenever(persister.read(barCode))
                .thenReturn(null)
                .thenReturn(source)

        whenever(persister.write(barCode, source))
                .thenReturn(true)
    }

    @Test
    fun testMoshiString() = runBlocking<Unit> {
        val store = ParsingStoreBuilder.builder<String, Foo>()
                .persister(persister)
                .fetcher(fetcher)
                .parser(MoshiParserFactory.createStringParser(Foo::class.java))
                .open()

        val result = store.get(barCode)

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
