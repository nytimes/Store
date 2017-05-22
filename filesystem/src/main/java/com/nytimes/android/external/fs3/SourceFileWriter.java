package com.nytimes.android.external.fs3;

import com.nytimes.android.external.fs3.filesystem.FileSystem;
import com.nytimes.android.external.store3.base.DiskWrite;
import com.nytimes.android.external.store3.base.impl.BarCode;

import okio.BufferedSource;

public class SourceFileWriter extends FSWriter<BarCode> implements DiskWrite<BufferedSource, BarCode> {

    public SourceFileWriter(FileSystem fileSystem) {
        super(fileSystem, new BarCodePathResolver());
    }

}
