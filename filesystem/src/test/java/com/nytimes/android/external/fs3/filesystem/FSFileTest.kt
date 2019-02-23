package com.nytimes.android.external.fs3.filesystem

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import okio.BufferedSource
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.mockito.Mockito.verify

class FSFileTest {

    @Rule
    @JvmField
    var folder = TemporaryFolder()

    private val source: BufferedSource = mock()

    private val fsFile by lazy { FSFile(folder.newFolder(), TEST_FILE_PATH) }

    @Test
    fun closeSourceAfterWrite() {
        whenever(source.read(any(), any())) doReturn -1L
        fsFile.write(source)
        verify(source).close()
    }

    companion object {
        private const val TEST_FILE_PATH = "/test_file"
    }
}
