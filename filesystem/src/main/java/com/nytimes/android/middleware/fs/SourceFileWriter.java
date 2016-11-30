package com.nytimes.android.middleware.fs;

import android.database.Observable;

import com.nytimes.android.external.store.base.DiskWrite;
import com.nytimes.android.external.store.base.impl.BarCode;

import java.util.concurrent.Callable;

import javax.inject.Inject;

import okio.BufferedSource;

import static com.nytimes.android.external.store.middleware.SourcePersister.pathForBarcode;
import static okio.Okio.buffer;

public class SourceFileWriter implements DiskWrite<BufferedSource> {

    final FileSystem fileSystem;

    @Inject
    public SourceFileWriter(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }


    @Override
    public Observable<Boolean> write(final BarCode barCode, final BufferedSource data) {
        return Observable.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                fileSystem.write(pathForBarcode(barCode), buffer(data));
                return true;
            }
        });
    }
}
