package com.nytimes.android.external.store3

import com.google.gson.Gson
import com.nhaarman.mockitokotlin2.mock
import com.nytimes.android.external.store3.middleware.GsonParserFactory
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import java.lang.reflect.Type

class GsonParserFactoryTest {

    @Rule
    @JvmField
    var expectedException = ExpectedException.none()

    private val type: Type = mock()
    private val gson = Gson()

    @Test
    fun shouldCreateParsersProperly() {
        GsonParserFactory.createReaderParser<Any>(gson, type)
        GsonParserFactory.createSourceParser<Any>(gson, type)
        GsonParserFactory.createStringParser<Any>(gson, type)
    }

    @Test
    fun shouldThrowExceptionWhenCreatingReaderWithNullType() {
        expectedException.expect(NullPointerException::class.java)
        GsonParserFactory.createReaderParser<Any>(gson, null!!)
    }

    @Test
    fun shouldThrowExceptionWhenCreatingReaderWithNullGson() {
        expectedException.expect(NullPointerException::class.java)
        GsonParserFactory.createReaderParser<Any>(null!!, type)
    }

    @Test
    fun shouldThrowExceptionWhenCreatingSourceWithNullType() {
        expectedException.expect(NullPointerException::class.java)
        GsonParserFactory.createSourceParser<Any>(gson, null!!)
    }

    @Test
    fun shouldThrowExceptionWhenCreatingSourceWithNullGson() {
        expectedException.expect(NullPointerException::class.java)
        GsonParserFactory.createSourceParser<Any>(null!!, type)
    }

    @Test
    fun shouldThrowExceptionWhenCreatingStringWithNullType() {
        expectedException.expect(NullPointerException::class.java)
        GsonParserFactory.createStringParser<Any>(gson, null!!)
    }

    @Test
    fun shouldThrowExceptionWhenCreatingStringWithNullGson() {
        expectedException.expect(NullPointerException::class.java)
        GsonParserFactory.createStringParser<Any>(null!!, type)
    }
}
