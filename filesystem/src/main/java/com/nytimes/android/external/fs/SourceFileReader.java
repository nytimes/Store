package com.nytimes.android.external.fs;

import com.nytimes.android.external.fs.filesystem.FileSystem;
import com.nytimes.android.external.store.base.DiskRead;
import com.nytimes.android.external.store.base.RecordState;
import com.nytimes.android.external.store.base.impl.BarCode;

import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import okio.BufferedSource;

public class SourceFileReader extends FSReader<BarCode> implements DiskRead<BufferedSource, BarCode> {
    public SourceFileReader(FileSystem fileSystem) {
        super(fileSystem, new BarCodePathResolver());
    }


    @Nonnull
    public RecordState getRecordState(@Nonnull BarCode barCode,
                                      @Nonnull TimeUnit expirationUnit,
                                      long expirationDuration) {
        String path = new BarCodePathResolver().resolve(barCode);
        return fileSystem.getRecordState(expirationUnit, expirationDuration, path);
    }
}
