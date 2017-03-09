package com.nytimes.android.external.fs;


import java.io.File;
import java.io.IOException;
import java.util.Stack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Util {

    @Nonnull
    public String simplifyPath(@Nonnull String path) {
        if (ifInvalidPATH(path)) {
            return "";
        }

        String delim = "[/]+";
        String[] arr = path.split(delim);

        Stack<String> stack = new Stack<>();

        fillStack(arr, stack);

        if (emptyStack(stack)) {
            return "/";
        }

        StringBuilder sb = new StringBuilder();

        for (String str : stack) {
            sb.append("/").append(str);
        }

        return sb.toString();
    }

    private boolean emptyStack(@Nonnull Stack<String> stack) {
        return stack.isEmpty();
    }

    private void fillStack(@Nonnull String[] arr, @Nonnull Stack<String> stack) {
        for (String str : arr) {
            if ("/".equals(str)) {
                continue;
            }
            if ("..".equals(str)) {
                if (!stack.isEmpty()) {
                    stack.pop();
                }
            } else if (!".".equals(str) && !str.isEmpty()) {
                stack.push(str);
            }
        }
    }

    private boolean ifInvalidPATH(@Nullable String path) {
        return path == null || path.length() == 0;
    }

    public void createParentDirs(@Nonnull File file) throws IOException {
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
