package com.nytimes.android.external.fs3

import com.nytimes.android.external.fs3.filesystem.FileSystem
import com.nytimes.android.external.store3.base.DiskRead
import com.nytimes.android.external.store3.base.RecordState
import com.nytimes.android.external.store3.base.impl.BarCode
import okio.BufferedSource
import java.util.concurrent.TimeUnit

class SourceFileReader @JvmOverloads constructor(fileSystem: FileSystem, pathResolver: PathResolver<BarCode> = BarCodePathResolver()) : FSReader<BarCode>(fileSystem, pathResolver), DiskRead<BufferedSource, BarCode> {

    fun getRecordState(barCode: BarCode,
                       expirationUnit: TimeUnit,
                       expirationDuration: Long): RecordState = fileSystem.getRecordState(expirationUnit, expirationDuration, SourcePersister.pathForBarcode(barCode))
}
