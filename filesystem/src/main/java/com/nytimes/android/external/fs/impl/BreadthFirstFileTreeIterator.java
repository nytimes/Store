package com.nytimes.android.external.fs.impl;

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

import java.io.File;
import java.util.Stack;
import java.util.Iterator;
import java.util.NoSuchElementException;

/** Breadth first iterator which iterates through all files which are decendents
 of the specified root file.

 @author Anthony Eden
 @since 1.1
 */

public class BreadthFirstFileTreeIterator implements Iterator{

    private File root;
    private int currentIndex = 0;
    private File[] currentList;
    private File nextFile;
    private Stack directories;
    private boolean endOfTree = false;

    /** Construct a new BreadthFirstFileTreeIterator with the specified root.

     @param root The root directory
     */

    public BreadthFirstFileTreeIterator(File root){
        this.root = root;
        this.currentList = root.listFiles();
        this.directories = new Stack();
    }

    /** Returns true if the iteration has more elements. (In other words,
     returns true if next would return an element rather than throwing
     an exception.)

     @return True if the iteration has more elements
     */

    public boolean hasNext(){
        if(endOfTree)
            return false;
        return getNextFile() != null;
    }

    /** Returns the next element in the iteration.

     @return The next element in the iteration
     */

    public Object next(){
        if(endOfTree)
            throw new NoSuchElementException();

        File file = getNextFile();
        if(file == null){
            throw new NoSuchElementException();
        }
        this.nextFile = null;
        return file;
    }

    /** Removes from the underlying collection the last element returned by
     the iterator (optional operation). This method can be called only
     once per call to next. The behavior of an iterator is unspecified
     if the underlying collection is modified while the iteration is in
     progress in any way other than by calling this method.

     @throws UnsupportedOperationException
     */

    public void remove(){
        throw new UnsupportedOperationException();
    }

    /** Get the next file.  If the value for the next file is null then the
     findNextFile() method is invoked to locate the next file.  A call
     to next() will return the next file and will null out the next file
     variable.

     @return The next file
     */

    protected File getNextFile(){
        if(nextFile == null){
            nextFile = findNextFile();
        }
        return nextFile;
    }

    /** Find the next file.

     @return The next file
     */

    protected File findNextFile(){
        while(currentIndex < currentList.length){
            if(currentList[currentIndex].isDirectory()){
                directories.push(currentList[currentIndex]);
                currentIndex++;
            } else {
                File file = currentList[currentIndex];
                currentIndex++;
                return file;
            }
        }

        while(!directories.empty()){
            File directory = (File)directories.remove(0);
            currentList = directory.listFiles();
            currentIndex = 0;
            File file = findNextFile();
            if(file != null){
                return file;
            }
        }

        endOfTree = true;

        return null;
    }

}
