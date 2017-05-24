package com.nytimes.android.external.fs;

import com.nytimes.android.external.fs.filesystem.FileSystem;
import com.nytimes.android.external.store.base.DiskAllErase;
import com.nytimes.android.external.store.base.DiskAllRead;


public class SourceFileAllReader extends FSAllReader implements DiskAllRead {

    public SourceFileAllReader(FileSystem fileSystem) {
        super(fileSystem);
    }
}
