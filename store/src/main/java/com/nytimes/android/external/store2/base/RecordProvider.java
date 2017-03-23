package com.nytimes.android.external.store2.base;

import javax.annotation.Nonnull;


public interface RecordProvider<Key> {
    @Nonnull
    RecordState getRecordState(@Nonnull Key key);
}
