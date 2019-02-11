package com.nytimes.android.external.fs3

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner

import io.reactivex.Single
import okio.BufferedSource

import org.assertj.core.api.Assertions.assertThat
import org.mockito.Matchers.any
import org.mockito.Mockito.`when`

@RunWith(MockitoJUnitRunner::class)
class ObjectToSourceTransformerTest {

    @Mock
    lateinit var mockBufferedParser: BufferedSourceAdapter<String>

    @Mock
    lateinit var mockBufferedSource: BufferedSource

    @Before
    @Throws(Exception::class)
    fun setUp() {
        `when`(mockBufferedParser.toJson(any())).thenReturn(mockBufferedSource)
    }

    @Test
    @Throws(Exception::class)
    fun testTransformer() {
        val source = Single.just("test")
                .compose(ObjectToSourceTransformer(mockBufferedParser))
                .blockingGet()

        assertThat(source).isEqualTo(mockBufferedSource)
    }
}
