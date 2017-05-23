/*
 * Copyright (C) 2014 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nytimes.android.external.cache3;


import java.util.Arrays;

import javax.annotation.Nonnull;

public final class MoreObjects {

  public static <T> T firstNonNull(T first, T second) {
    return first != null ? first : Preconditions.checkNotNull(second);
  }

  /**
   * Creates an instance of {@link ToStringHelper}.
   *
   * <p>This is helpful for implementing {@link Object#toString()}.
   * Specification by example: <pre>   {@code
   *   // Returns "ClassName{}"
   *   MoreObjects.toStringHelper(this)
   *       .toString();
   *
   *   // Returns "ClassName{x=1}"
   *   MoreObjects.toStringHelper(this)
   *       .add("x", 1)
   *       .toString();
   *
   *   // Returns "MyObject{x=1}"
   *   MoreObjects.toStringHelper("MyObject")
   *       .add("x", 1)
   *       .toString();
   *
   *   // Returns "ClassName{x=1, y=foo}"
   *   MoreObjects.toStringHelper(this)
   *       .add("x", 1)
   *       .add("y", "foo")
   *       .toString();
   *
   *   // Returns "ClassName{x=1}"
   *   MoreObjects.toStringHelper(this)
   *       .omitNullValues()
   *       .add("x", 1)
   *       .add("y", null)
   *       .toString();
   *   }}</pre>
   *
   * <p>Note that in GWT, class names are often obfuscated.
   *
   * @param self the object to generate the string for (typically {@code this}), used only for its
   *     class name
   * @since 18.0 (since 2.0 as {@code Objects.toStringHelper()}).
   */
  @Nonnull
  public static ToStringHelper toStringHelper(@Nonnull Object self) {
    return new ToStringHelper(self.getClass().getSimpleName());
  }

// --Commented out by Inspection START (11/29/16, 5:04 PM):
//  /**
//   * Creates an instance of {@link ToStringHelper} in the same manner as {@link
//   * #toStringHelper(Object)}, but using the simple name of {@code clazz} instead of using an
//   * instance's {@link Object#getClass()}.
//   *
//   * <p>Note that in GWT, class names are often obfuscated.
//   *
//   * @param clazz the {@link Class} of the instance
//   * @since 18.0 (since 7.0 as {@code Objects.toStringHelper()}).
//   */
//  public static ToStringHelper toStringHelper(Class<?> clazz) {
//    return new ToStringHelper(clazz.getSimpleName());
//  }
// --Commented out by Inspection STOP (11/29/16, 5:04 PM)

// --Commented out by Inspection START (11/29/16, 5:04 PM):
//  /**
//   * Creates an instance of {@link ToStringHelper} in the same manner as {@link
//   * #toStringHelper(Object)}, but using {@code className} instead of using an instance's {@link
//   * Object#getClass()}.
//   *
//   * @param className the name of the instance type
//   * @since 18.0 (since 7.0 as {@code Objects.toStringHelper()}).
//   */
//  public static ToStringHelper toStringHelper(String className) {
//    return new ToStringHelper(className);
//  }
// --Commented out by Inspection STOP (11/29/16, 5:04 PM)

  /**
   * Support class for {@link MoreObjects#toStringHelper}.
   *
   * @author Jason Lee
   * @since 18.0 (since 2.0 as {@code Objects.ToStringHelper}).
   */
  public static final class ToStringHelper {
    private final String className;
    @Nonnull
    private ValueHolder holderHead = new ValueHolder();
    @Nonnull
    private ValueHolder holderTail = holderHead;
    private boolean omitNullValues = false;

    /**
     * Use {@link MoreObjects#toStringHelper(Object)} to create an instance.
     */
    private ToStringHelper(@Nonnull String className) {
      this.className = Preconditions.checkNotNull(className);
    }

// --Commented out by Inspection START (11/29/16, 5:04 PM):
//    /**
//     * Configures the {@link ToStringHelper} so {@link #toString()} will ignore
//     * properties with null value. The order of calling this method, relative
//     * to the {@code add()}/{@code addValue()} methods, is not significant.
//     *
//     * @since 18.0 (since 12.0 as {@code Objects.ToStringHelper.omitNullValues()}).
//     */
//    public ToStringHelper omitNullValues() {
//      omitNullValues = true;
//      return this;
//    }
// --Commented out by Inspection STOP (11/29/16, 5:04 PM)

    /**
     * Adds a name/value pair to the formatted output in {@code name=value}
     * format. If {@code value} is {@code null}, the string {@code "null"}
     * is used, unless {@link #omitNullValues()} is called, in which case this
     * name/value pair will not be added.
     */
    @Nonnull
    public ToStringHelper add(@Nonnull String name, Object value) {
      return addHolder(name, value);
    }

// --Commented out by Inspection START (11/29/16, 5:04 PM):
//    /**
//     * Adds a name/value pair to the formatted output in {@code name=value}
//     * format.
//     *
//     * @since 18.0 (since 11.0 as {@code Objects.ToStringHelper.add()}).
//     */
//    public ToStringHelper add(String name, boolean value) {
//      return addHolder(name, String.valueOf(value));
//    }
// --Commented out by Inspection STOP (11/29/16, 5:04 PM)

// --Commented out by Inspection START (11/29/16, 5:04 PM):
//    /**
//     * Adds a name/value pair to the formatted output in {@code name=value}
//     * format.
//     *
//     * @since 18.0 (since 11.0 as {@code Objects.ToStringHelper.add()}).
//     */
//    public ToStringHelper add(String name, char value) {
//      return addHolder(name, String.valueOf(value));
//    }
// --Commented out by Inspection STOP (11/29/16, 5:04 PM)

// --Commented out by Inspection START (11/29/16, 5:04 PM):
//    /**
//     * Adds a name/value pair to the formatted output in {@code name=value}
//     * format.
//     *
//     * @since 18.0 (since 11.0 as {@code Objects.ToStringHelper.add()}).
//     */
//    public ToStringHelper add(String name, double value) {
//      return addHolder(name, String.valueOf(value));
//    }
// --Commented out by Inspection STOP (11/29/16, 5:04 PM)

// --Commented out by Inspection START (11/29/16, 5:04 PM):
//    /**
//     * Adds a name/value pair to the formatted output in {@code name=value}
//     * format.
//     *
//     * @since 18.0 (since 11.0 as {@code Objects.ToStringHelper.add()}).
//     */
//    public ToStringHelper add(String name, float value) {
//      return addHolder(name, String.valueOf(value));
//    }
// --Commented out by Inspection STOP (11/29/16, 5:04 PM)

    /**
     * Adds a name/value pair to the formatted output in {@code name=value}
     * format.
     *
     * @since 18.0 (since 11.0 as {@code Objects.ToStringHelper.add()}).
     */
    @Nonnull
    public ToStringHelper add(@Nonnull String name, int value) {
      return addHolder(name, String.valueOf(value));
    }

    /**
     * Adds a name/value pair to the formatted output in {@code name=value}
     * format.
     *
     * @since 18.0 (since 11.0 as {@code Objects.ToStringHelper.add()}).
     */
    @Nonnull
    public ToStringHelper add(@Nonnull String name, long value) {
      return addHolder(name, String.valueOf(value));
    }

    /**
     * Adds an unnamed value to the formatted output.
     *
     * <p>It is strongly encouraged to use {@link #add(String, Object)} instead
     * and give value a readable name.
     */
    @Nonnull
    public ToStringHelper addValue(Object value) {
      return addHolder(value);
    }

// --Commented out by Inspection START (11/29/16, 5:04 PM):
//    /**
//     * Adds an unnamed value to the formatted output.
//     *
//     * <p>It is strongly encouraged to use {@link #add(String, boolean)} instead
//     * and give value a readable name.
//     *
//     * @since 18.0 (since 11.0 as {@code Objects.ToStringHelper.addValue()}).
//     */
//    public ToStringHelper addValue(boolean value) {
//      return addHolder(String.valueOf(value));
//    }
// --Commented out by Inspection STOP (11/29/16, 5:04 PM)

// --Commented out by Inspection START (11/29/16, 5:04 PM):
//    /**
//     * Adds an unnamed value to the formatted output.
//     *
//     * <p>It is strongly encouraged to use {@link #add(String, char)} instead
//     * and give value a readable name.
//     *
//     * @since 18.0 (since 11.0 as {@code Objects.ToStringHelper.addValue()}).
//     */
//    public ToStringHelper addValue(char value) {
//      return addHolder(String.valueOf(value));
//    }
// --Commented out by Inspection STOP (11/29/16, 5:04 PM)

// --Commented out by Inspection START (11/29/16, 5:04 PM):
//    /**
//     * Adds an unnamed value to the formatted output.
//     *
//     * <p>It is strongly encouraged to use {@link #add(String, double)} instead
//     * and give value a readable name.
//     *
//     * @since 18.0 (since 11.0 as {@code Objects.ToStringHelper.addValue()}).
//     */
//    public ToStringHelper addValue(double value) {
//      return addHolder(String.valueOf(value));
//    }
// --Commented out by Inspection STOP (11/29/16, 5:04 PM)

// --Commented out by Inspection START (11/29/16, 5:04 PM):
//    /**
//     * Adds an unnamed value to the formatted output.
//     *
//     * <p>It is strongly encouraged to use {@link #add(String, float)} instead
//     * and give value a readable name.
//     *
//     * @since 18.0 (since 11.0 as {@code Objects.ToStringHelper.addValue()}).
//     */
//    public ToStringHelper addValue(float value) {
//      return addHolder(String.valueOf(value));
//    }
// --Commented out by Inspection STOP (11/29/16, 5:04 PM)

// --Commented out by Inspection START (11/29/16, 5:04 PM):
//    /**
//     * Adds an unnamed value to the formatted output.
//     *
//     * <p>It is strongly encouraged to use {@link #add(String, int)} instead
//     * and give value a readable name.
//     *
//     * @since 18.0 (since 11.0 as {@code Objects.ToStringHelper.addValue()}).
//     */
//    public ToStringHelper addValue(int value) {
//      return addHolder(String.valueOf(value));
//    }
// --Commented out by Inspection STOP (11/29/16, 5:04 PM)

// --Commented out by Inspection START (11/29/16, 5:04 PM):
//    /**
//     * Adds an unnamed value to the formatted output.
//     *
//     * <p>It is strongly encouraged to use {@link #add(String, long)} instead
//     * and give value a readable name.
//     *
//     * @since 18.0 (since 11.0 as {@code Objects.ToStringHelper.addValue()}).
//     */
//    public ToStringHelper addValue(long value) {
//      return addHolder(String.valueOf(value));
//    }
// --Commented out by Inspection STOP (11/29/16, 5:04 PM)

    /**
     * Returns a string in the format specified by
     * {@link MoreObjects#toStringHelper(Object)}.
     *
     * <p>After calling this method, you can keep adding more properties to later
     * call toString() again and get a more complete representation of the
     * same object; but properties cannot be removed, so this only allows
     * limited reuse of the helper instance. The helper allows duplication of
     * properties (multiple name/value pairs with the same name can be added).
     */
    @Override
    public String toString() {
      // create a copy to keep it consistent in case value changes
      boolean omitNullValuesSnapshot = omitNullValues;
      String nextSeparator = "";
      StringBuilder builder = new StringBuilder(32).append(className).append('{');
      for (ValueHolder valueHolder = holderHead.next;
          valueHolder != null;
          valueHolder = valueHolder.next) {
        Object value = valueHolder.value;
        if (!omitNullValuesSnapshot || value != null) {
          builder.append(nextSeparator);
          nextSeparator = ", ";

          if (valueHolder.name != null) {
            builder.append(valueHolder.name).append('=');
          }
          if (value != null && value.getClass().isArray()) {
            Object[] objectArray = {value};
            String arrayString = Arrays.deepToString(objectArray);
            builder.append(arrayString.substring(1, arrayString.length() - 1));
          } else {
            builder.append(value);
          }
        }
      }
      return builder.append('}').toString();
    }

    @Nonnull
    private ValueHolder addHolder() {
      ValueHolder valueHolder = new ValueHolder();
      holderTail = holderTail.next = valueHolder;
      return valueHolder;
    }

    @Nonnull
    private ToStringHelper addHolder(Object value) {
      ValueHolder valueHolder = addHolder();
      valueHolder.value = value;
      return this;
    }

    @Nonnull
    private ToStringHelper addHolder(@Nonnull String name, Object value) {
      ValueHolder valueHolder = addHolder();
      valueHolder.value = value;
      valueHolder.name = Preconditions.checkNotNull(name);
      return this;
    }

    private static final class ValueHolder {
      String name;
      Object value;
      ValueHolder next;
    }
  }

  private MoreObjects() {}
}
