package com.nytimes.android.external.store3.storecache;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class RecordPolicyTest {

    private final long nowInMs = System.currentTimeMillis();

    @Test
    public void basicValid() {
        StoreRecord oneMin = StoreRecord.create(1, TimeUnit.MINUTES);
        Assertions.assertThat(RecordPolicy.hasExpired(oneMin, nowInMs)).isFalse();
    }

    @Test
    public void basicExpired() {
        long fiveMinutesAgoMs = System.currentTimeMillis() - TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES);
        StoreRecord oneMin = StoreRecord.create(1, TimeUnit.MINUTES, fiveMinutesAgoMs);
        Assertions.assertThat(RecordPolicy.hasExpired(oneMin, nowInMs)).isTrue();
    }

    @Test
    public void basicAccessValid() {
        StoreRecord oneMin = StoreRecord.create(1, TimeUnit.MINUTES);
        oneMin.setRecordPolicy(RecordPolicy.ExpireAfterAccess);
        Assertions.assertThat(RecordPolicy.hasExpired(oneMin, nowInMs)).isFalse();
    }

    @Test
    public void basicAccessExpired() {
        long fiveMinutesAgoMs = System.currentTimeMillis() - TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES);
        StoreRecord oneMin = StoreRecord.create(1, TimeUnit.MINUTES, fiveMinutesAgoMs);
        oneMin.setRecordPolicy(RecordPolicy.ExpireAfterAccess);
        Assertions.assertThat(RecordPolicy.hasExpired(oneMin, nowInMs)).isTrue();
    }

}
