package com.nytimes.android.external.fs3.filesystem

import okio.Buffer
import okio.BufferedSource
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import java.io.IOException

class FSFileTest {

    @Rule
    @JvmField
    var folder = TemporaryFolder()

    @Mock
    internal lateinit var source: BufferedSource

    private lateinit var fsFile: FSFile

    @Before
    @Throws(IOException::class)
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        val root = folder.newFolder()
        fsFile = FSFile(root, TEST_FILE_PATH)
    }

    @Test
    @Throws(IOException::class)
    fun closeSourceAfterWrite() {
        `when`(source.read(ArgumentMatchers.any(Buffer::class.java), ArgumentMatchers.anyLong())).thenReturn(-1L)
        fsFile.write(source)
        verify<BufferedSource>(source).close()
    }

    companion object {
        private const val TEST_FILE_PATH = "/test_file"
    }
}
