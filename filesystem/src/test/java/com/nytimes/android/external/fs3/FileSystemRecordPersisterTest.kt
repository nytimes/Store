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
import org.mockito.Mockito.inOrder
import java.io.FileNotFoundException
import java.util.concurrent.TimeUnit

class FileSystemRecordPersisterTest {

    private val fileSystem: FileSystem = mock()
    private val bufferedSource: BufferedSource = mock()

    private val simple = BarCode("type", "key")
    private val resolvedPath = BarCodePathResolver().resolve(simple)
    private val fileSystemPersister = FileSystemRecordPersister.create(fileSystem,
            BarCodePathResolver(),
            1, TimeUnit.DAYS)

    @Test
    fun readExists() = runBlocking<Unit> {
        whenever(fileSystem.exists(resolvedPath))
                .thenReturn(true)
        whenever(fileSystem.read(resolvedPath)).thenReturn(bufferedSource)

        val returnedValue = fileSystemPersister.read(simple)
        assertThat(returnedValue).isEqualTo(bufferedSource)
    }

    @Test
    fun readDoesNotExist() = runBlocking<Unit> {
        whenever(fileSystem.exists(resolvedPath))
                .thenReturn(false)

        try {
            fileSystemPersister.read(simple)
            fail()
        } catch (e: FileNotFoundException) {
        }
    }

    @Test
    fun writeThenRead() = runBlocking<Unit> {
        whenever(fileSystem.read(resolvedPath)).thenReturn(bufferedSource)
        whenever(fileSystem.exists(resolvedPath)).thenReturn(true)
        fileSystemPersister.write(simple, bufferedSource)
        val source = fileSystemPersister.read(simple)
        val inOrder = inOrder(fileSystem)
        inOrder.verify<FileSystem>(fileSystem).write(resolvedPath, bufferedSource)
        inOrder.verify<FileSystem>(fileSystem).exists(resolvedPath)
        inOrder.verify<FileSystem>(fileSystem).read(resolvedPath)

        assertThat(source).isEqualTo(bufferedSource)
    }

    @Test
    fun freshTest() = runBlocking<Unit> {
        whenever(fileSystem.getRecordState(TimeUnit.DAYS, 1L, resolvedPath))
                .thenReturn(RecordState.FRESH)

        assertThat(fileSystemPersister.getRecordState(simple)).isEqualTo(RecordState.FRESH)
    }

    @Test
    fun staleTest() = runBlocking<Unit> {
        whenever(fileSystem.getRecordState(TimeUnit.DAYS, 1L, resolvedPath))
                .thenReturn(RecordState.STALE)

        assertThat(fileSystemPersister.getRecordState(simple)).isEqualTo(RecordState.STALE)
    }

    @Test
    fun missingTest() = runBlocking<Unit> {
        whenever(fileSystem.getRecordState(TimeUnit.DAYS, 1L, resolvedPath))
                .thenReturn(RecordState.MISSING)

        assertThat(fileSystemPersister.getRecordState(simple)).isEqualTo(RecordState.MISSING)
    }
}
