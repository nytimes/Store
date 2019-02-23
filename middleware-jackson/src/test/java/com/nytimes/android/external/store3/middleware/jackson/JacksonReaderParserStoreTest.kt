package com.nytimes.android.external.store3.middleware.jackson


import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nytimes.android.external.store3.base.Fetcher
import com.nytimes.android.external.store3.base.Persister
import com.nytimes.android.external.store3.base.impl.BarCode
import com.nytimes.android.external.store3.base.impl.StoreBuilder
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import java.io.Reader
import java.io.StringReader

class JacksonReaderParserStoreTest {
    @Rule
    @JvmField
    var expectedException = ExpectedException.none()

    private val source = StringReader(sourceString)

    private val fetcher: Fetcher<Reader, BarCode> = mock {
        onBlocking { it.fetch(barCode) } doReturn source as Reader
    }
    private val persister: Persister<Reader, BarCode> = mock {
        onBlocking { it.read(barCode) }
                .doReturn(null)
                .doReturn(source)

        onBlocking { it.write(barCode, source) } doReturn true
    }
    private val barCode = BarCode("value", KEY)

    @Test
    fun testDefaultJacksonReaderParser() = runBlocking<Unit> {
        val parser = JacksonParserFactory.createReaderParser<Foo>(Foo::class.java)
        val store = StoreBuilder.parsedWithKey<BarCode, Reader, Foo>()
                .persister(persister)
                .fetcher(fetcher)
                .parser(parser)
                .open()

        val result = store.get(barCode)
        validateFoo(result)
        verify(fetcher, times(1)).fetch(barCode)
    }

    @Test
    fun testCustomJsonFactoryReaderParser() = runBlocking<Unit> {
        val jsonFactory = JsonFactory()

        val parser = JacksonParserFactory.createReaderParser<Foo>(jsonFactory, Foo::class.java)

        val store = StoreBuilder.parsedWithKey<BarCode, Reader, Foo>()
                .persister(persister)
                .fetcher(fetcher)
                .parser(parser)
                .open()

        val result = store.get(barCode)
        validateFoo(result)
        verify(fetcher, times(1)).fetch(barCode)
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
        JacksonParserFactory.createReaderParser<Any>((null as JsonFactory?)!!, Foo::class.java)
    }

    @Test
    fun testNullTypeWithValidJsonFactory() {
        expectedException.expect(NullPointerException::class.java)
        JacksonParserFactory.createReaderParser<Any>(JsonFactory(), null!!)
    }

    @Test
    fun testNullObjectMapper() {
        expectedException.expect(NullPointerException::class.java)
        JacksonParserFactory.createReaderParser<Any>((null as ObjectMapper?)!!, Foo::class.java)
    }

    @Test
    fun testNullType() {
        expectedException.expect(NullPointerException::class.java)
        JacksonParserFactory.createStringParser<Any>(null!!)
    }

    companion object {
        private val KEY = "key"
        private val sourceString = "{\"number\":123,\"string\":\"abc\",\"bars\":[{\"string\":\"def\"},{\"string\":\"ghi\"}]}"
    }
}
