package com.nytimes.android.external.fs;

import com.nytimes.android.external.store.base.DiskWrite;
import com.nytimes.android.external.store.base.impl.BarCode;

import java.util.concurrent.Callable;

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
