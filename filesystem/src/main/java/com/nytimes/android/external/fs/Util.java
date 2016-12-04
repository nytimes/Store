package com.nytimes.android.external.fs;

import java.io.File;
import java.io.IOException;
import java.util.Stack;

import static dagger.internal.Preconditions.checkNotNull;

public class Util {

    public String simplifyPath(String path) {
        if (path == null || path.length() == 0) {
            return "";
        }

        String delim = "[/]+";
        String[] arr = path.split(delim);

        Stack<String> stack = new Stack<String>();

        for (String str : arr) {
            if(str.equals("/")){
                continue;
            }
            if (str.equals("..")) {
                if (!stack.isEmpty()) {
                    stack.pop();
                }
            } else if (!str.equals(".") && !str.isEmpty()) {
                stack.push(str);
            }
        }

        StringBuilder sb = new StringBuilder();
        if (stack.isEmpty()) {
            return "/";
        }

        for (String str : stack) {
            sb.append("/" + str);
        }

        return sb.toString();
    }

    public void createParentDirs(File file) throws IOException {
        checkNotNull(file);
        File parent = file.getCanonicalFile().getParentFile();
        if (parent == null) {
      /*
       * The given directory is a filesystem root. All zero of its ancestors
       * exist. This doesn't mean that the root itself exists -- consider x:\ on
       * a Windows machine without such a drive -- or even that the caller can
       * create it, but this method makes no such guarantees even for non-root
       * files.
       */
            return;
        }
        parent.mkdirs();
        if (!parent.isDirectory()) {
            throw new IOException("Unable to create parent directories of " + file);
        }
    }
}
