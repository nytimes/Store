package com.nytimes.android.external.store3.storecache;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

public class StoreCacheTest {

    @Test
    public void basicTest() {

        StoreCache<String, String> cache = StoreCacheBuilder.newBuilder().build();
        cache.put("key", "value");
        Assertions.assertThat(cache.getIfPresent("key")).isNotNull();
        cache.invalidate("key");
        Assertions.assertThat(cache.getIfPresent("key")).isNull();
    }

    @Test
    public void basicTestGet() throws ExecutionException {

        StoreCache<String, String> cache = StoreCacheBuilder.newBuilder().build();
        cache.get("key", () -> "value");
        Assertions.assertThat(cache.getIfPresent("key")).isNotNull();
        cache.invalidate("key");
        Assertions.assertThat(cache.getIfPresent("key")).isNull();
    }

    @Test
    public void basicClear() {

        StoreCache<String, String> cache = StoreCacheBuilder.newBuilder().build();
        cache.put("key", "value");
        Assertions.assertThat(cache.getIfPresent("key")).isNotNull();
        cache.clearAll();
        Assertions.assertThat(cache.getIfPresent("key")).isNull();
    }

}
