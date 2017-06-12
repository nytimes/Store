package com.nytimes.android.external.store3.base.impl

import org.apache.commons.lang3.builder.EqualsBuilder
import org.junit.Test
import java.util.concurrent.TimeUnit

/**
 * Spec for FluentMemoryPolicyBuilder.
 */
class FluentMemoryPolicyBuilderSpec {
    @Test
    fun shouldBuildAnEquivalentObjectWithAfterWrite() {
        val expireAfterWriteValue = 10L
        val expireAfterTimeUnitValue = TimeUnit.MILLISECONDS
        val maxSizeValue = 1000L
        val javaResult = MemoryPolicy.builder()
                .setExpireAfterWrite(expireAfterWriteValue)
                .setExpireAfterTimeUnit(expireAfterTimeUnitValue)
                .setMemorySize(maxSizeValue)
                .build()
        val kotlinResult = FluentMemoryPolicyBuilder.build {
            expireAfterWrite = expireAfterWriteValue
            expireAfterTimeUnit = expireAfterTimeUnitValue
            memorySize = maxSizeValue
        }
        assertEquivalent(javaResult, kotlinResult)
    }

    @Test
    fun shouldBuildAnEquivalentObjectWithAfterAccess() {
        val expireAfterAccessValue = 20L
        val expireAfterTimeUnitValue = TimeUnit.MILLISECONDS
        val maxSizeValue = 1000L
        val javaResult = MemoryPolicy.builder()
                .setExpireAfterAccess(expireAfterAccessValue)
                .setExpireAfterTimeUnit(expireAfterTimeUnitValue)
                .setMemorySize(maxSizeValue)
                .build()
        val kotlinResult = FluentMemoryPolicyBuilder.build {
            expireAfterAccess = expireAfterAccessValue
            expireAfterTimeUnit = expireAfterTimeUnitValue
            memorySize = maxSizeValue
        }
        assertEquivalent(javaResult, kotlinResult)
    }

    private fun assertEquivalent(expected: MemoryPolicy, actual: MemoryPolicy) =
            EqualsBuilder.reflectionEquals(expected, actual)
}

