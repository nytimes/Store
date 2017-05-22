package com.nytimes.android.external.cache3;

import javax.annotation.Nonnull;

public final class Ascii {

    private Ascii() {
    }


    /**
     * Returns a copy of the input string in which all {@linkplain #isUpperCase(char) uppercase ASCII
     * characters} have been converted to lowercase. All other characters are copied without
     * modification.
     */
    @Nonnull
    public static String toLowerCase(@Nonnull String string) {
        int length = string.length();
        for (int i = 0; i < length; i++) {
            if (isUpperCase(string.charAt(i))) {
                char[] chars = string.toCharArray();
                for (; i < length; i++) {
                    char c = chars[i];
                    if (isUpperCase(c)) {
                        chars[i] = (char) (c ^ 0x20);
                    }
                }
                return String.valueOf(chars);
            }
        }
        return string;
    }

    /**
     * Indicates whether {@code c} is one of the twenty-six uppercase ASCII alphabetic characters
     * between {@code 'A'} and {@code 'Z'} inclusive. All others (including non-ASCII characters)
     * return {@code false}.
     */
    public static boolean isUpperCase(char c) {
        return (c >= 'A') && (c <= 'Z');
    }

}
