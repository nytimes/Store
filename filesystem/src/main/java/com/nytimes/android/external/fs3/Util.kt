package com.nytimes.android.external.fs3


import java.io.File
import java.io.IOException
import java.util.*

object Util {

    fun simplifyPath(path: String): String {
        if (ifInvalidPATH(path)) {
            return ""
        }

        val delim = "[/]+"
        val arr = path.split(delim.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        val stack = Stack<String>()

        fillStack(arr, stack)

        if (emptyStack(stack)) {
            return "/"
        }

        val sb = StringBuilder()

        for (str in stack) {
            sb.append("/").append(str)
        }

        return sb.toString()
    }

    private fun emptyStack(stack: Stack<String>): Boolean {
        return stack.isEmpty()
    }

    private fun fillStack(arr: Array<String>, stack: Stack<String>) {
        for (str in arr) {
            if ("/" == str) {
                continue
            }
            if (".." == str) {
                if (!stack.isEmpty()) {
                    stack.pop()
                }
            } else if ("." != str && !str.isEmpty()) {
                stack.push(str)
            }
        }
    }

    private fun ifInvalidPATH(path: String?): Boolean =
            path == null || path.isEmpty()

    @Throws(IOException::class)
    fun createParentDirs(file: File) {
        val parent = file.canonicalFile.parentFile
                ?: /*
       * The given directory is a filesystem root. All zero of its ancestors
       * exist. This doesn't mean that the root itself exists -- consider x:\ on
       * a Windows machine without such a drive -- or even that the caller can
       * create it, but this method makes no such guarantees even for non-root
       * files.
       */
                return
        parent.mkdirs()
        if (!parent.isDirectory) {
            throw IOException("Unable to create parent directories of $file")
        }
    }
}
