package com.nytimes.android.external.store3.base.impl

import com.nytimes.android.external.store3.base.Fetcher
import com.nytimes.android.external.store3.base.Parser
import com.nytimes.android.external.store3.util.KeyParser
import org.assertj.core.api.Assertions.assertThat
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
        assertThat(sut.persister).isNull()
    }
    @Test
    fun defaultMemoryPolicyIsNull() {
        @Suppress("UNCHECKED_CAST")
        val sut = StoreParameters(mock(Fetcher::class.java) as Fetcher<Any, Any>)
        assertThat(sut.memoryPolicy).isNull()
    }
    @Test
    fun defaultStalePolicyIsUnspecified() {
        @Suppress("UNCHECKED_CAST")
        val sut = StoreParameters(mock(Fetcher::class.java) as Fetcher<Any, Any>)
        assertThat(sut.stalePolicy).isEqualTo(StalePolicy.UNSPECIFIED)
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
        assertThat(sut.parser).isNull()
    }

    @Test
    fun defaultParsersIsNull() {
        val sut = ParsableStoreParameters<Any, Any, Any>(mock(Fetcher::class.java) as Fetcher<Any, Any>)
        assertThat(sut.parsers).isNull()
    }

    @Test
    fun shouldMakeParserNullWhenParsersIsAssigned() {
        val sut = ParsableStoreParameters<Any, Any, Any>(mock(Fetcher::class.java) as Fetcher<Any, Any>)
        sut.parser = mock(KeyParser::class.java) as KeyParser<Any, Any, Any>
        sut.parsers = mock(List::class.java) as List<Parser<Any, Any>>
        assertThat(sut.parser).isNull()
    }

    @Test
    fun shouldMakeParsersNullWhenParserIsAssigned() {
        val sut = ParsableStoreParameters<Any, Any, Any>(mock(Fetcher::class.java) as Fetcher<Any, Any>)
        sut.parsers = mock(List::class.java) as List<Parser<Any, Any>>
        sut.parser = mock(KeyParser::class.java) as KeyParser<Any, Any, Any>
        assertThat(sut.parsers).isNull()
    }
}
