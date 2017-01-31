package com.nytimes.android.external.fs;

import com.nytimes.android.external.fs.filesystem.FileSystem;
import com.nytimes.android.external.store.base.DiskWrite;
import com.nytimes.android.external.store.base.impl.BarCode;

import java.util.concurrent.Callable;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import okio.BufferedSource;
import rx.Observable;

import static com.nytimes.android.external.fs.SourcePersister.pathForBarcode;
import static okio.Okio.buffer;

public class SourceFileWriter implements DiskWrite<BufferedSource> {

    final FileSystem fileSystem;

    @Inject
    public SourceFileWriter(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }


    @Nonnull
    @Override
    public Observable<Boolean> write(@Nonnull final BarCode barCode, @Nonnull final BufferedSource data) {
        return Observable.fromCallable(new Callable<Boolean>() {
            @Nonnull
            @Override
            @SuppressWarnings("PMD.SignatureDeclareThrowsException")
            public Boolean call() throws Exception {
                fileSystem.write(pathForBarcode(barCode), buffer(data));
                return true;
            }
        });
    }
}
