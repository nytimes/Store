package com.nytimes.android.external.store3.base.impl

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.concurrent.TimeUnit

/**
 * Spec for MemoryPolicyParameters.
 */
class MemoryPolicyParametersSpec {
    @Test
    fun shouldHaveExpireAfterWriteBeMemoryPolicyDEFAULT_POLICY() {
        val sut = MemoryPolicyParameters()
        assertEquals(MemoryPolicy.DEFAULT_POLICY, sut.expireAfterWrite)
    }

    @Test
    fun shouldHaveExpireAfterAccessBeMemoryPolicyDEFAULT_POLICY() {
        val sut = MemoryPolicyParameters()
        assertEquals(MemoryPolicy.DEFAULT_POLICY, sut.expireAfterAccess)
    }

    @Test
    fun shouldHaveExpireAfterTimeUnitBeTimeUnitSECONDS() {
        val sut = MemoryPolicyParameters()
        assertEquals(TimeUnit.SECONDS, sut.expireAfterTimeUnit)
    }

    @Test
    fun shouldHaveMemorySizeBe1() {
        val sut = MemoryPolicyParameters()
        assertEquals(1, sut.memorySize)
    }

    @Test
    fun expiredAfterWriteMustBeGreaterThanOrEqualTo0() {
        val sut = MemoryPolicyParameters()
        val validValue = 82L
        val invalidValue = -1L
        sut.expireAfterWrite = validValue
        assertEquals(validValue, sut.expireAfterWrite)
        sut.expireAfterWrite = invalidValue
        assertEquals(validValue, sut.expireAfterWrite)
    }

    @Test
    fun expiredAfterAccessMustBeGreaterThanOrEqualTo0() {
        val sut = MemoryPolicyParameters()
        val validValue = 82L
        val invalidValue = -1L
        sut.expireAfterAccess = validValue
        assertEquals(validValue, sut.expireAfterAccess)
        sut.expireAfterAccess = invalidValue
        assertEquals(validValue, sut.expireAfterAccess)
    }

    @Test
    fun memorySizeShouldBeGreaterThanOrEqualTo1() {
        val sut = MemoryPolicyParameters()
        val validValue = 3L
        val invalidValue = -1L
        sut.memorySize = validValue
        assertEquals(validValue, sut.memorySize)
        sut.memorySize = invalidValue
        assertEquals(validValue, sut.memorySize)
    }
}
