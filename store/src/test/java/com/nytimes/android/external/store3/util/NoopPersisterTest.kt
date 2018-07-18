package com.nytimes.android.external.store3.util

import com.nytimes.android.external.store3.base.impl.BarCode
import com.nytimes.android.external.store3.base.impl.MemoryPolicy

import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

import org.assertj.core.api.Assertions.assertThat
import java.util.concurrent.TimeUnit

class NoopPersisterTest {

    @Rule
    @JvmField
    var exception = ExpectedException.none()

    @Test
    fun writeReadTest() {
        val barCode = BarCode("key", "value")
        val persister = NoopPersister.create<String, BarCode>()
        val success = persister.write(barCode, "foo").blockingGet()
        assertThat(success).isTrue()
        val rawValue = persister.read(barCode).blockingGet()
        assertThat(rawValue).isEqualTo("foo")
    }

    @Test
    fun noopParserFuncTest() {
        val noopParserFunc = NoopParserFunc<String, String>()
        val input = "foo"
        val output = noopParserFunc.apply(input)
        assertThat(input).isEqualTo(output)
        //intended object ref comparison
        assertThat(input).isSameAs(output)
    }

    // https://github.com/NYTimes/Store/issues/312
    @Test
    fun testReadingOfMemoryPolicies() {
        val expireAfterWritePolicy = MemoryPolicy.builder()
                .setExpireAfterWrite(1)
                .setExpireAfterTimeUnit(TimeUnit.HOURS)
                .build()
        NoopPersister.create<Any, Any>(expireAfterWritePolicy)

        val expireAfterAccessPolicy = MemoryPolicy.builder()
                .setExpireAfterAccess(1)
                .setExpireAfterTimeUnit(TimeUnit.HOURS)
                .build()
        NoopPersister.create<Any, Any>(expireAfterAccessPolicy)

        exception.expect(IllegalArgumentException::class.java)
        exception.expectMessage("No expiry policy set")
        val incompletePolicy = MemoryPolicy.builder()
                .setExpireAfterTimeUnit(TimeUnit.HOURS)
                .build()
        NoopPersister.create<Any, Any>(incompletePolicy)
    }
}
