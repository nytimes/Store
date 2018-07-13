package com.nytimes.android.external.fs3

import com.nytimes.android.external.fs3.filesystem.FileSystem
import com.nytimes.android.external.store3.base.impl.BarCode

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.mockito.Mock
import org.mockito.MockitoAnnotations

import java.io.FileNotFoundException
import java.io.IOException

import okio.BufferedSource

import org.assertj.core.api.Assertions.assertThat
import org.mockito.Mockito.`when`

class SourcePersisterTest {

    @Rule
    @JvmField
    var expectedException = ExpectedException.none()

    @Mock
    lateinit var fileSystem: FileSystem
    @Mock
    lateinit var bufferedSource: BufferedSource

    private lateinit var sourcePersister: SourcePersister
    private val simple = BarCode("type", "key")

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        sourcePersister = SourcePersister(fileSystem)
    }

    @Test
    @Throws(FileNotFoundException::class)
    fun readExists() {
        `when`(fileSystem.exists(simple.toString()))
                .thenReturn(true)
        `when`(fileSystem.read(simple.toString())).thenReturn(bufferedSource)

        val returnedValue = sourcePersister.read(simple).blockingGet()
        assertThat(returnedValue).isEqualTo(bufferedSource)
    }

    @Test
    @Throws(FileNotFoundException::class)
    fun readDoesNotExist() {
        `when`(fileSystem.exists(SourcePersister.pathForBarcode(simple)))
                .thenReturn(false)

        sourcePersister.read(simple).test().assertError(FileNotFoundException::class.java)
    }

    @Test
    @Throws(IOException::class)
    fun write() {
        assertThat(sourcePersister.write(simple, bufferedSource).blockingGet()).isTrue()
    }

    @Test
    fun pathForBarcode() {
        assertThat(SourcePersister.pathForBarcode(simple)).isEqualTo("typekey")
    }
}
