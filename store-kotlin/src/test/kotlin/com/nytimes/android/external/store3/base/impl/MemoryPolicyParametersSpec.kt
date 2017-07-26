package com.nytimes.android.external.store3.base.impl

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.util.concurrent.TimeUnit

/**
 * Spec for MemoryPolicyParameters.
 */
class MemoryPolicyParametersSpec {
    @Test
    fun shouldHaveExpireAfterWriteBeMemoryPolicyDEFAULT_POLICY() {
        val sut = MemoryPolicyParameters()
        assertThat(sut.expireAfterWrite).isEqualTo(MemoryPolicy.DEFAULT_POLICY)
    }

    @Test
    fun shouldHaveExpireAfterAccessBeMemoryPolicyDEFAULT_POLICY() {
        val sut = MemoryPolicyParameters()
        assertThat(sut.expireAfterAccess).isEqualTo(MemoryPolicy.DEFAULT_POLICY)
    }

    @Test
    fun shouldHaveExpireAfterTimeUnitBeTimeUnitSECONDS() {
        val sut = MemoryPolicyParameters()
        assertThat(sut.expireAfterTimeUnit).isEqualTo(TimeUnit.SECONDS)
    }

    @Test
    fun shouldHaveMemorySizeBe1() {
        val sut = MemoryPolicyParameters()
        assertThat(sut.memorySize).isEqualTo(1)
    }

    @Test
    fun expiredAfterWriteMustBeGreaterThanOrEqualTo0() {
        val sut = MemoryPolicyParameters()
        val validValue = 82L
        val invalidValue = -1L
        sut.expireAfterWrite = validValue
        assertThat(sut.expireAfterWrite).isEqualTo(validValue)
        sut.expireAfterWrite = invalidValue
        assertThat(sut.expireAfterWrite).isEqualTo(validValue)
    }

    @Test
    fun expiredAfterAccessMustBeGreaterThanOrEqualTo0() {
        val sut = MemoryPolicyParameters()
        val validValue = 82L
        val invalidValue = -1L
        sut.expireAfterAccess = validValue
        assertThat(sut.expireAfterAccess).isEqualTo(validValue)
        sut.expireAfterAccess = invalidValue
        assertThat(sut.expireAfterAccess).isEqualTo(validValue)
    }

    @Test
    fun memorySizeShouldBeGreaterThanOrEqualTo1() {
        val sut = MemoryPolicyParameters()
        val validValue = 3L
        val invalidValue = -1L
        sut.memorySize = validValue
        assertThat(sut.memorySize).isEqualTo(validValue)
        sut.memorySize = invalidValue
        assertThat(sut.memorySize).isEqualTo(validValue)
    }
}
