/*
 * Copyright (C) 2011 The Guava Authors
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

/**
 * The reason why a cached entry was removed.
 *
 * @author Charles Fry
 * @since 10.0
 */
public enum RemovalCause {

  EXPLICIT {
    @Override
    boolean wasEvicted() {
      return false;
    }
  },

  REPLACED {
    @Override
    boolean wasEvicted() {
      return false;
    }
  },


  COLLECTED {
    @Override
    boolean wasEvicted() {
      return true;
    }
  },

  EXPIRED {
    @Override
    boolean wasEvicted() {
      return true;
    }
  },


  SIZE {
    @Override
    boolean wasEvicted() {
      return true;
    }
  };

  /**
   * Returns {@code true} if there was an automatic removal due to eviction (the cause is neither
   * {@link #EXPLICIT} nor {@link #REPLACED}).
   */
  abstract boolean wasEvicted();
}
