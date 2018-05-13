package com.nytimes.android.external.store3.base.impl;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class MemoryPolicyBuilderTest {

    @Test
    public void testBuildExpireAfterWriteMemoryPolicy() {
        MemoryPolicy policy = MemoryPolicy.builder()
                .setExpireAfterWrite(4L)
                .build();

        assertThat(policy.getExpireAfterWrite()).isEqualTo(4L);
        assertThat(policy.getExpireAfterTimeUnit()).isEqualTo(TimeUnit.SECONDS);
        assertThat(policy.isDefaultWritePolicy()).isFalse();
        assertThat(policy.getExpireAfterAccess()).isEqualTo(MemoryPolicy.DEFAULT_POLICY);
    }

    @Test
    public void testBuildExpireAfterAccessMemoryPolicy() {
        MemoryPolicy policy = MemoryPolicy.builder()
                .setExpireAfterAccess(4L)
                .build();

        assertThat(policy.getExpireAfterAccess()).isEqualTo(4L);
        assertThat(policy.getExpireAfterTimeUnit()).isEqualTo(TimeUnit.SECONDS);
        assertThat(policy.isDefaultWritePolicy()).isTrue();
        assertThat(policy.getExpireAfterWrite()).isEqualTo(MemoryPolicy.DEFAULT_POLICY);
    }

    @Test(expected = IllegalStateException.class)
    public void testCannotSetBothExpirationPolicies() {
        MemoryPolicy.builder()
                .setExpireAfterAccess(4L)
                .setExpireAfterWrite(4L)
                .build();
    }

    @Test
    public void testBuilderSetsExpireAfterTimeUnit() {
        MemoryPolicy policy = MemoryPolicy.builder()
                .setExpireAfterTimeUnit(TimeUnit.MINUTES)
                .build();

        assertThat(policy.getExpireAfterTimeUnit()).isEqualTo(TimeUnit.MINUTES);
    }

    @Test
    public void testBuilderSetsMemorySize() {
        MemoryPolicy policy = MemoryPolicy.builder()
                .setMemorySize(10L)
                .build();

        assertThat(policy.getMaxSize()).isEqualTo(10L);
    }

    @Test
    public void testDefaultMemorySizeIfNotSet() {
        MemoryPolicy policy = MemoryPolicy.builder()
            .build();

        assertThat(policy.getMaxSize()).isEqualTo(1L);
    }
}
