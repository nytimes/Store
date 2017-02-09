package com.nytimes.android.external.store.base;

import javax.annotation.Nonnull;

/**
 * Created by 206847 on 2/6/17.
 */
public interface RecordProvider<Key> {
    RecordState getRecordState(@Nonnull Key key);
}
