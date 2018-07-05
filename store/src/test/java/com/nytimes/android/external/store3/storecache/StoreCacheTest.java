package com.nytimes.android.external.store3.storecache;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class StoreCacheTest {

    @Test
    public void getIfPresentAndInvalidate() {

        StoreCache<String, String> cache = StoreCacheBuilder.newBuilder().build();
        cache.put("key", "value");
        Assertions.assertThat(cache.getIfPresent("key")).isNotNull();
        cache.invalidate("key");
        Assertions.assertThat(cache.getIfPresent("key")).isNull();
    }

    @Test
    public void getAndInvalidate() throws ExecutionException {

        StoreCache<String, String> cache = StoreCacheBuilder.newBuilder().build();
        cache.get("key", () -> "value");
        Assertions.assertThat(cache.getIfPresent("key")).isNotNull();
        cache.invalidate("key");
        Assertions.assertThat(cache.getIfPresent("key")).isNull();
    }

    @Test
    public void putAndClearAll() {
        StoreCache<String, String> cache = StoreCacheBuilder.newBuilder().build();
        cache.put("key", "value");
        Assertions.assertThat(cache.getIfPresent("key")).isNotNull();
        cache.clearAll();
        Assertions.assertThat(cache.getIfPresent("key")).isNull();
        Assertions.assertThat(cache.asMap().keySet().size()).isZero();
    }

    @Test
    public void expireAfterAccess() {
        StoreCache<String, String> cache = StoreCacheBuilder.newBuilder()
                .expireAfterAccess(1L, TimeUnit.MINUTES)
                .timeProvider(() -> System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES))
                .build();

        cache.put("key", "value");
        Assertions.assertThat(cache.getIfPresent("key")).isNull();
    }

    @Test
    public void maxCountBasicExpireAfterWrite() {
        StoreCache<String, String> cache = StoreCacheBuilder.newBuilder()
                .maximumSize(2)
                .build();

        cache.put("key", "value");
        cache.put("key1", "value1");
        cache.put("key2", "value2");
        Assertions.assertThat(cache.getIfPresent("key")).isNull();
        Assertions.assertThat(cache.getIfPresent("key1")).isNotNull();
        Assertions.assertThat(cache.getIfPresent("key2")).isNotNull();
    }

    @Test
    public void maxCountBasicExpireAfterAccess() {
        StoreCache<String, String> cache = StoreCacheBuilder.newBuilder()
                .expireAfterAccess(1, TimeUnit.HOURS)
                .timeProvider(new TimeProvider() {
                    @Override
                    public long provideTime() {
                        return System.currentTimeMillis() + 25;
                    }
                })
                .maximumSize(2)
                .build();

        cache.put("key", "value");
        cache.put("key1", "value1");
        cache.getIfPresent("key");
        cache.put("key2", "value2");

        Assertions.assertThat(cache.getIfPresent("key")).isNotNull();
        Assertions.assertThat(cache.getIfPresent("key1")).isNull();
        Assertions.assertThat(cache.getIfPresent("key2")).isNotNull();
    }

    @Test
    public void expireAfterAccessGoodThenBad() {

        StoreCache<String, String> cache = StoreCacheBuilder.newBuilder()
                .expireAfterAccess(1L, TimeUnit.MINUTES)
                .timeProvider(new TimeProvider() {
                    int callCount = 0;
                    @Override
                    public long provideTime() {
                        if (callCount == 0) {
                            callCount++;
                            return System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(30, TimeUnit.SECONDS);
                        } else if (callCount == 1) {
                            callCount++;
                            return System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(1, TimeUnit.MINUTES);
                        } else {
                            return System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES);
                        }
                    }
                })
                .build();

        cache.put("key", "value");
        Assertions.assertThat(cache.getIfPresent("key")).isNotNull();
        Assertions.assertThat(cache.getIfPresent("key")).isNotNull();
        Assertions.assertThat(cache.getIfPresent("key")).isNull();
    }

    @Test
    public void expireAfterWriteGoodThenBad() {

        StoreCache<String, String> cache = StoreCacheBuilder.newBuilder()
                .expireAfterWrite(1L, TimeUnit.MINUTES)
                .timeProvider(new TimeProvider() {
                    int callCount = 0;
                    @Override
                    public long provideTime() {
                        if (callCount == 0) {
                            callCount++;
                            return System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(30, TimeUnit.SECONDS);
                        } else if (callCount == 1) {
                            callCount++;
                            return System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(65, TimeUnit.SECONDS);
                        } else {
                            return System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES);
                        }
                    }
                })
                .build();

        cache.put("key", "value");
        Assertions.assertThat(cache.getIfPresent("key")).isNotNull();
        Assertions.assertThat(cache.getIfPresent("key")).isNull();
        Assertions.assertThat(cache.getIfPresent("key")).isNull();
    }

}
