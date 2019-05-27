package com.nytimes.android.external.fs3.filesystem

/*
 * Copyright 2004-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

//package org.jpublish.util;


import java.io.File
import java.util.*

/**
 * Breadth first iterator which iterates through all files which are decendents
 * of the specified root file.
 *
 * @author Anthony Eden
 * @since 1.1
 */
internal class BreadthFirstFileTreeIterator
/**
 * Construct a new BreadthFirstFileTreeIterator with the specified root.
 *
 * @param root The root directory
 */
(root: File) : Iterator<Any?> {
    private var currentIndex = 0
    private var currentList = arrayOfNulls<File>(0)
    private var nextFile: File? = null
    private val directories: Stack<File>
    private var endOfTree = false

    init {
        val listedFiles = root.listFiles()
        if (listedFiles != null) {
            currentList = listedFiles.copyOf(listedFiles.size)
        }
        this.directories = Stack()
    }

    /**
     * Returns true if the iteration has more elements. (In other words,
     * returns true if next would return an element rather than throwing
     * an exception.)
     *
     * @return True if the iteration has more elements
     */
    override fun hasNext(): Boolean =
            !endOfTree && getNextFile() != null

    /**
     * Returns the next element in the iteration.
     *
     * @return The next element in the iteration
     */

    override fun next(): Any? {
        if (endOfTree) {
            throw NoSuchElementException()
        }

        val file = getNextFile() ?: throw NoSuchElementException()
        this.nextFile = null
        return file
    }

    /**
     * Get the next file.  If the value for the next file is null then the
     * findNextFile() method is invoked to locate the next file.  A call
     * to next() will return the next file and will null out the next file
     * variable.
     *
     * @return The next file
     */
    private fun getNextFile(): File? {
        if (nextFile == null) {
            nextFile = findNextFile()
        }
        return nextFile
    }

    /**
     * Find the next file.
     *
     * @return The next file
     */
    private fun findNextFile(): File? {
        while (currentIndex < currentList.size) {
            if (currentList[currentIndex]?.isDirectory == true) {
                directories.push(currentList[currentIndex])
                currentIndex++
            } else {
                val file = currentList[currentIndex]
                currentIndex++
                return file
            }
        }

        while (!directories.empty()) {
            val directory = directories.removeAt(0)
            currentList = directory.listFiles()
            currentIndex = 0
            val file = findNextFile()
            if (file != null) {
                return file
            }
        }

        endOfTree = true

        return null
    }

}