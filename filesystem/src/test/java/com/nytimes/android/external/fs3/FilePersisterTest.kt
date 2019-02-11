package com.nytimes.android.external.fs3

import com.nytimes.android.external.fs3.filesystem.FileSystem
import com.nytimes.android.external.store3.base.Persister
import com.nytimes.android.external.store3.base.impl.BarCode
import okio.BufferedSource
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.inOrder
import org.mockito.MockitoAnnotations
import java.io.FileNotFoundException
import java.io.IOException

class FilePersisterTest {

    @Mock
    lateinit var fileSystem: FileSystem
    @Mock
    lateinit var bufferedSource: BufferedSource

    private val simple = BarCode("type", "key")
    private val resolvedPath = BarCodePathResolver().resolve(simple)
    lateinit var fileSystemPersister: Persister<BufferedSource, BarCode>

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        fileSystemPersister = FileSystemPersister.create(fileSystem, BarCodePathResolver())
    }

    @Test
    @Throws(FileNotFoundException::class)
    fun readExists() {
        `when`(fileSystem.exists(resolvedPath))
                .thenReturn(true)
        `when`(fileSystem.read(resolvedPath)).thenReturn(bufferedSource)

        val returnedValue = fileSystemPersister.read(simple).blockingGet()
        assertThat(returnedValue).isEqualTo(bufferedSource)
    }

    @Test
    @Throws(FileNotFoundException::class)
    fun readDoesNotExist() {
        `when`(fileSystem.exists(resolvedPath))
                .thenReturn(false)

        fileSystemPersister.read(simple).test().assertError(FileNotFoundException::class.java)
    }

    @Test
    @Throws(IOException::class)
    fun writeThenRead() {
        `when`(fileSystem.read(resolvedPath)).thenReturn(bufferedSource)
        `when`(fileSystem.exists(resolvedPath)).thenReturn(true)
        fileSystemPersister.write(simple, bufferedSource).blockingGet()
        val source = fileSystemPersister.read(simple).blockingGet()
        val inOrder = inOrder(fileSystem)
        inOrder.verify<FileSystem>(fileSystem).write(resolvedPath, bufferedSource)
        inOrder.verify<FileSystem>(fileSystem).exists(resolvedPath)
        inOrder.verify<FileSystem>(fileSystem).read(resolvedPath)

        assertThat(source).isEqualTo(bufferedSource)
    }
}
