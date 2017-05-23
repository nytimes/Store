package com.nytimes.android.external.fs3;

import com.nytimes.android.external.fs3.filesystem.FileSystem;
import com.nytimes.android.external.store3.base.DiskRead;
import com.nytimes.android.external.store3.base.RecordState;
import com.nytimes.android.external.store3.base.impl.BarCode;

import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import okio.BufferedSource;

import static com.nytimes.android.external.fs3.SourcePersister.pathForBarcode;

public class SourceFileReader extends FSReader<BarCode> implements DiskRead<BufferedSource, BarCode> {

    public SourceFileReader(FileSystem fileSystem) {
        super(fileSystem, new BarCodePathResolver());
    }


    @Nonnull
    public RecordState getRecordState(@Nonnull BarCode barCode,
                                      @Nonnull TimeUnit expirationUnit,
                                      long expirationDuration) {
        return fileSystem.getRecordState(expirationUnit, expirationDuration, pathForBarcode(barCode));
    }
}
