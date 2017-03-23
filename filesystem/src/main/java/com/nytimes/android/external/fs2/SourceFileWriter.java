package com.nytimes.android.external.fs2;

import com.nytimes.android.external.fs2.filesystem.FileSystem;
import com.nytimes.android.external.store2.base.DiskWrite;
import com.nytimes.android.external.store2.base.impl.BarCode;

import okio.BufferedSource;

public class SourceFileWriter extends FSWriter<BarCode> implements DiskWrite<BufferedSource, BarCode> {

    public SourceFileWriter(FileSystem fileSystem) {
        super(fileSystem, new BarCodePathResolver());
    }

}
