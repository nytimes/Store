package com.nytimes.android.external.fs3

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.nytimes.android.external.fs3.filesystem.FileSystem
import com.nytimes.android.external.store3.base.RecordState
import com.nytimes.android.external.store3.base.impl.BarCode
import junit.framework.Assert.fail
import kotlinx.coroutines.runBlocking
import okio.BufferedSource
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.io.FileNotFoundException
import java.util.concurrent.TimeUnit

class RecordPersisterTest {

    private val fileSystem: FileSystem = mock()
    private val bufferedSource: BufferedSource = mock()

    private val sourcePersister = RecordPersister(fileSystem, 1L, TimeUnit.DAYS)
    private val simple = BarCode("type", "key")

    @Test
    fun readExists() = runBlocking<Unit> {
        whenever(fileSystem.exists(simple.toString()))
                .thenReturn(true)
        whenever(fileSystem.read(simple.toString())).thenReturn(bufferedSource)

        val returnedValue = sourcePersister.read(simple)
        assertThat(returnedValue).isEqualTo(bufferedSource)
    }

    @Test
    fun freshTest() = runBlocking<Unit> {
        whenever(fileSystem.getRecordState(TimeUnit.DAYS, 1L, SourcePersister.pathForBarcode(simple)))
                .thenReturn(RecordState.FRESH)

        assertThat(sourcePersister.getRecordState(simple)).isEqualTo(RecordState.FRESH)
    }

    @Test
    fun staleTest() = runBlocking<Unit> {
        whenever(fileSystem.getRecordState(TimeUnit.DAYS, 1L, SourcePersister.pathForBarcode(simple)))
                .thenReturn(RecordState.STALE)

        assertThat(sourcePersister.getRecordState(simple)).isEqualTo(RecordState.STALE)
    }

    @Test
    fun missingTest() = runBlocking<Unit> {
        whenever(fileSystem.getRecordState(TimeUnit.DAYS, 1L, SourcePersister.pathForBarcode(simple)))
                .thenReturn(RecordState.MISSING)

        assertThat(sourcePersister.getRecordState(simple)).isEqualTo(RecordState.MISSING)
    }

    @Test
    fun readDoesNotExist() = runBlocking<Unit> {
        whenever(fileSystem.exists(SourcePersister.pathForBarcode(simple)))
                .thenReturn(false)

        try {
            sourcePersister.read(simple)
            fail()
        } catch (e: FileNotFoundException) {
        }
    }

    @Test
    fun write() = runBlocking<Unit> {
        assertThat(sourcePersister.write(simple, bufferedSource)).isTrue()
    }

    @Test
    fun pathForBarcode() = runBlocking<Unit> {
        assertThat(SourcePersister.pathForBarcode(simple)).isEqualTo("typekey")
    }
}
