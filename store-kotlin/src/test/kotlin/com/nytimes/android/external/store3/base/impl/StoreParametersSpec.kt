package com.nytimes.android.external.store3.base.impl

import com.nytimes.android.external.store3.base.Fetcher
import com.nytimes.android.external.store3.base.Parser
import com.nytimes.android.external.store3.util.KeyParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.mockito.Mockito.mock

/**
 * Spec for StoreParameters.
 */
open class StoreParametersSpec {
    @Test
    fun defaultPersisterIsNull() {
        @Suppress("UNCHECKED_CAST")
        val sut = StoreParameters(mock(Fetcher::class.java) as Fetcher<Any, Any>)
        assertNull(sut.persister)
    }
    @Test
    fun defaultMemoryPolicyIsNull() {
        @Suppress("UNCHECKED_CAST")
        val sut = StoreParameters(mock(Fetcher::class.java) as Fetcher<Any, Any>)
        assertNull(sut.memoryPolicy)
    }
    @Test
    fun defaultStalePolicyIsUnspecified() {
        @Suppress("UNCHECKED_CAST")
        val sut = StoreParameters(mock(Fetcher::class.java) as Fetcher<Any, Any>)
        assertEquals(StalePolicy.UNSPECIFIED, sut.stalePolicy)
    }
}

/**
 * Spec for ParsableStoreParameters.
 */
@Suppress("UNCHECKED_CAST")
class ParsableStoreParametersSpec : StoreParametersSpec() {
    @Test
    fun defaultParserIsNull() {
        val sut = ParsableStoreParameters<Any, Any, Any>(mock(Fetcher::class.java) as Fetcher<Any, Any>)
        assertNull(sut.parser)
    }

    @Test
    fun defaultParsersIsNull() {
        val sut = ParsableStoreParameters<Any, Any, Any>(mock(Fetcher::class.java) as Fetcher<Any, Any>)
        assertNull(sut.parsers)
    }

    @Test
    fun shouldMakeParserNullWhenParsersIsAssigned() {
        val sut = ParsableStoreParameters<Any, Any, Any>(mock(Fetcher::class.java) as Fetcher<Any, Any>)
        sut.parser = mock(KeyParser::class.java) as KeyParser<Any, Any, Any>
        sut.parsers = mock(List::class.java) as List<Parser<Any, Any>>
        assertNull(sut.parser)
    }

    @Test
    fun shouldMakeParsersNullWhenParserIsAssigned() {
        val sut = ParsableStoreParameters<Any, Any, Any>(mock(Fetcher::class.java) as Fetcher<Any, Any>)
        sut.parsers = mock(List::class.java) as List<Parser<Any, Any>>
        sut.parser = mock(KeyParser::class.java) as KeyParser<Any, Any, Any>
        assertNull(sut.parsers)
    }
}
