package com.nytimes.android.external.store3.storecache;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class RecordPolicyTest {

    private final long nowInMs = System.currentTimeMillis();

    @Test
    public void basicValid() {
        StoreRecord oneMin = new StoreRecord(RecordPolicy.ExpireAfterWrite, 1, TimeUnit.MINUTES, "");
        Assertions.assertThat(RecordPolicy.hasExpired(oneMin, nowInMs)).isFalse();
    }

    @Test
    public void basicExpired() {
        long fiveMinutesFromNowMs = System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES);
        StoreRecord oneMin = new StoreRecord(RecordPolicy.ExpireAfterWrite, 1, TimeUnit.MINUTES, "");
        Assertions.assertThat(RecordPolicy.hasExpired(oneMin, fiveMinutesFromNowMs)).isTrue();
    }

    @Test
    public void basicAccessValid() {
        StoreRecord oneMin = new StoreRecord(RecordPolicy.ExpireAfterAccess, 1, TimeUnit.MINUTES, "");
        Assertions.assertThat(RecordPolicy.hasExpired(oneMin, nowInMs)).isFalse();
    }

    @Test
    public void basicAccessExpired() {
        long fiveMinutesFromNowMs = System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES);
        StoreRecord oneMin = new StoreRecord(RecordPolicy.ExpireAfterAccess, 1, TimeUnit.MINUTES, "");
        Assertions.assertThat(RecordPolicy.hasExpired(oneMin, fiveMinutesFromNowMs)).isTrue();
    }

}
