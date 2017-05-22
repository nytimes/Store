/*
 * Copyright (C) 2010 The Guava Authors
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


import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


/**
 * A strategy for determining whether two instances are considered equivalent. Examples of
 * equivalences are the {@linkplain #identity() identity equivalence} and {@linkplain #equals equals
 * equivalence}.
 *
 * @author Bob Lee
 * @author Ben Yu
 * @author Gregory Kick
 * @since 10.0 (<a href="https://github.com/google/guava/wiki/Compatibility"
 *        >mostly source-compatible</a> since 4.0)
 */

public abstract class Equivalence<T> {
  /**
   * Constructor for use by subclasses.
   */
  protected Equivalence() {}

  /**
   * Returns {@code true} if the given objects are considered equivalent.
   *
   * <p>The {@code equivalent} method implements an equivalence relation on object references:
   *
   * <ul>
   * <li>It is <i>reflexive</i>: for any reference {@code x}, including null, {@code
   *     equivalent(x, x)} returns {@code true}.
   * <li>It is <i>symmetric</i>: for any references {@code x} and {@code y}, {@code
   *     equivalent(x, y) == equivalent(y, x)}.
   * <li>It is <i>transitive</i>: for any references {@code x}, {@code y}, and {@code z}, if
   *     {@code equivalent(x, y)} returns {@code true} and {@code equivalent(y, z)} returns {@code
   *     true}, then {@code equivalent(x, z)} returns {@code true}.
   * <li>It is <i>consistent</i>: for any references {@code x} and {@code y}, multiple invocations
   *     of {@code equivalent(x, y)} consistently return {@code true} or consistently return {@code
   *     false} (provided that neither {@code x} nor {@code y} is modified).
   * </ul>
   */
  public final boolean equivalent(  T a,   T b) {
    if (a == b) {
      return true;
    }
    if (a == null || b == null) {
      return false;
    }
    return doEquivalent(a, b);
  }

  /**
   * Returns {@code true} if {@code a} and {@code b} are considered equivalent.
   *
   * <p>Called by {@link #equivalent}. {@code a} and {@code b} are not the same
   * object and are not nulls.
   *
   * @since 10.0 (previously, subclasses would override equivalent())
   */
  protected abstract boolean doEquivalent(T a, T b);

  /**
   * Returns a hash code for {@code t}.
   *
   * <p>The {@code hash} has the following properties:
   * <ul>
   * <li>It is <i>consistent</i>: for any reference {@code x}, multiple invocations of
   *     {@code hash(x}} consistently return the same value provided {@code x} remains unchanged
   *     according to the definition of the equivalence. The hash need not remain consistent from
   *     one execution of an application to another execution of the same application.
   * <li>It is <i>distributable across equivalence</i>: for any references {@code x} and {@code y},
   *     if {@code equivalent(x, y)}, then {@code hash(x) == hash(y)}. It is <i>not</i> necessary
   *     that the hash be distributable across <i>inequivalence</i>. If {@code equivalence(x, y)}
   *     is false, {@code hash(x) == hash(y)} may still be true.
   * <li>{@code hash(null)} is {@code 0}.
   * </ul>
   */
  public final int hash(@Nullable T t) {
    if (t == null) {
      return 0;
    }
    return doHash(t);
  }

  /**
   * Returns a hash code for non-null object {@code t}.
   *
   * <p>Called by {@link #hash}.
   *
   * @since 10.0 (previously, subclasses would override hash())
   */
  protected abstract int doHash(T t);

  /**
   * Returns an equivalence that delegates to {@link Object#equals} and {@link Object#hashCode}.
   * {@link Equivalence#equivalent} returns {@code true} if both values are null, or if neither
   * value is null and {@link Object#equals} returns {@code true}. {@link Equivalence#hash} returns
   * {@code 0} if passed a null value.
   *
   * @since 13.0
   * @since 8.0 (in Equivalences with null-friendly behavior)
   * @since 4.0 (in Equivalences)
   */
  @Nonnull
  public static Equivalence<Object> equals() {
    return Equals.INSTANCE;
  }

  /**
   * Returns an equivalence that uses {@code ==} to compare values and {@link
   * System#identityHashCode(Object)} to compute the hash code.  {@link Equivalence#equivalent}
   * returns {@code true} if {@code a == b}, including in the case that a and b are both null.
   *
   * @since 13.0
   * @since 4.0 (in Equivalences)
   */
  @Nonnull
  public static Equivalence<Object> identity() {
    return Identity.INSTANCE;
  }

  static final class Equals extends Equivalence<Object> implements Serializable {

    static final Equals INSTANCE = new Equals();

    @Override
    protected boolean doEquivalent(@Nonnull Object a, Object b) {
      return a.equals(b);
    }

    @Override
    protected int doHash(@Nonnull Object o) {
      return o.hashCode();
    }

    @Nonnull
    private Object readResolve() {
      return INSTANCE;
    }

    private static final long serialVersionUID = 1;
  }

  static final class Identity extends Equivalence<Object> implements Serializable {

    static final Identity INSTANCE = new Identity();

    @Override
    protected boolean doEquivalent(Object a, Object b) {
      return false;
    }

    @Override
    protected int doHash(Object o) {
      return System.identityHashCode(o);
    }

    @Nonnull
    private Object readResolve() {
      return INSTANCE;
    }

    private static final long serialVersionUID = 1;
  }
}
