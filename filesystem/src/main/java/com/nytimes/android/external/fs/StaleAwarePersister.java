package com.nytimes.android.external.fs;

import com.nytimes.android.external.fs.filesystem.FileSystem;
import com.nytimes.android.external.store.base.impl.BarCode;

import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;
import javax.inject.Inject;

public class StaleAwarePersister extends SourcePersister {

    @Nonnull
    private final TimeUnit expirationUnit;
    private final long expirationDuration;

    @Inject
    public StaleAwarePersister(FileSystem fileSystem,
                               @Nonnull TimeUnit expirationUnit,
                               long expirationDuration) {
        super(fileSystem);
        this.expirationDuration = expirationDuration;
        this.expirationUnit = expirationUnit;
    }

    public boolean isRecordStale(@Nonnull BarCode barCode) {
        return sourceFileReader.isStale(barCode, expirationUnit, expirationDuration);
    }
}
