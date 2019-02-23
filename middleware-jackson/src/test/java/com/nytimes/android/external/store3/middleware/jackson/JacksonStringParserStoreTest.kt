package com.nytimes.android.external.store3.middleware.jackson

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.nytimes.android.external.store3.base.Fetcher
import com.nytimes.android.external.store3.base.Persister
import com.nytimes.android.external.store3.base.impl.BarCode
import com.nytimes.android.external.store3.base.impl.StoreBuilder
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.mockito.Mockito.times
import org.mockito.Mockito.verify

class JacksonStringParserStoreTest {
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
    fun testDefaultJacksonStringParser() = runBlocking<Unit> {
        val store = StoreBuilder.parsedWithKey<BarCode, String, Foo>()
                .persister(persister)
                .fetcher(fetcher)
                .parser(JacksonParserFactory.createStringParser(Foo::class.java))
                .open()

        val result = store.get(barCode)
        validateFoo(result)
        verify<Fetcher<String, BarCode>>(fetcher, times(1)).fetch(barCode)
    }

    @Test
    fun testCustomJsonFactoryStringParser() = runBlocking<Unit> {
        val jsonFactory = JsonFactory()

        val parser = JacksonParserFactory.createStringParser<Foo>(jsonFactory, Foo::class.java)

        val store = StoreBuilder.parsedWithKey<BarCode, String, Foo>()
                .persister(persister)
                .fetcher(fetcher)
                .parser(parser)
                .open()

        val result = store.get(barCode)
        validateFoo(result)
        verify<Fetcher<String, BarCode>>(fetcher, times(1)).fetch(barCode)
    }

    private fun validateFoo(foo: Foo) {
        assertNotNull(foo)
        assertEquals(foo.number.toLong(), 123)
        assertEquals(foo.string, "abc")
        assertEquals(foo.bars.size.toLong(), 2)
        assertEquals(foo.bars[0].string, "def")
        assertEquals(foo.bars[1].string, "ghi")
    }

    @Test
    fun testNullJsonFactory() {
        expectedException.expect(NullPointerException::class.java)
        JacksonParserFactory.createStringParser<Any>((null as JsonFactory?)!!, Foo::class.java)
    }

    @Test
    fun testNullTypeWithValidJsonFactory() {
        expectedException.expect(NullPointerException::class.java)
        JacksonParserFactory.createStringParser<Any>(JsonFactory(), null!!)
    }

    @Test
    fun testNullObjectMapper() {
        expectedException.expect(NullPointerException::class.java)
        JacksonParserFactory.createStringParser<Any>((null as ObjectMapper?)!!, Foo::class.java)
    }

    @Test
    fun testNullType() {
        expectedException.expect(NullPointerException::class.java)
        JacksonParserFactory.createStringParser<Any>(null!!)
    }

    companion object {

        private val KEY = "key"
        private val source = "{\"number\":123,\"string\":\"abc\",\"bars\":[{\"string\":\"def\"},{\"string\":\"ghi\"}]}"
    }
}
