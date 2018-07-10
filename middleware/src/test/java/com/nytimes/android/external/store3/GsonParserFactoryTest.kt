package com.nytimes.android.external.store3

import com.google.gson.Gson
import com.nytimes.android.external.store3.middleware.GsonParserFactory

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.mockito.Mock
import org.mockito.MockitoAnnotations

import java.lang.reflect.Type

class GsonParserFactoryTest {

    @Rule
    @JvmField
    var expectedException = ExpectedException.none()

    @Mock
    lateinit var type: Type
    private val gson = Gson()

    @Before
    @Throws(Exception::class)
    fun setUp() {
        MockitoAnnotations.initMocks(this)
    }

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
