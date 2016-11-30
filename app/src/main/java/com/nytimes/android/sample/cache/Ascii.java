package com.nytimes.android.sample.cache;

public final class Ascii {

  private Ascii() {}

//
//  /**
//   * Line Feed ('\n'): A format effector which controls the movement of
//   * the printing position to the next printing line.  (Applicable also to
//   * display devices.) Where appropriate, this character may have the
//   * meaning "New Line" (NL), a format effector which controls the
//   * movement of the printing point to the first printing position on the
//   * next printing line.  Use of this convention requires agreement
//   * between sender and recipient of data.
//   *
//   * @since 8.0
//   */
//  public static final byte LF = 10;

  /**
   * Returns a copy of the input string in which all {@linkplain #isUpperCase(char) uppercase ASCII
   * characters} have been converted to lowercase. All other characters are copied without
   * modification.
   */
  public static String toLowerCase(String string) {
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

// --Commented out by Inspection START (11/29/16, 5:19 PM):
//  /**
//   * Indicates whether {@code c} is one of the twenty-six lowercase ASCII alphabetic characters
//   * between {@code 'a'} and {@code 'z'} inclusive. All others (including non-ASCII characters)
//   * return {@code false}.
//   */
//  public static boolean isLowerCase(char c) {
//    // Note: This was benchmarked against the alternate expression "(char)(c - 'a') < 26" (Nov '13)
//    // and found to perform at least as well, or better.
//    return (c >= 'a') && (c <= 'z');
//  }
// --Commented out by Inspection STOP (11/29/16, 5:19 PM)

  /**
   * Indicates whether {@code c} is one of the twenty-six uppercase ASCII alphabetic characters
   * between {@code 'A'} and {@code 'Z'} inclusive. All others (including non-ASCII characters)
   * return {@code false}.
   */
  public static boolean isUpperCase(char c) {
    return (c >= 'A') && (c <= 'Z');
  }

}
