package com.nytimes.android.external.store3.base.impl

import com.nytimes.android.external.store3.annotations.Experimental
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

/**
 * A parameter box for MemoryPolicy instantiation.
 */
@Experimental
class MemoryPolicyParameters {
    var expireAfterWrite by Delegates.vetoable(MemoryPolicy.DEFAULT_POLICY) {
        _, _, newValue -> newValue >= 0
    }
    var expireAfterAccess by Delegates.vetoable(MemoryPolicy.DEFAULT_POLICY) {
        _, _, newValue -> newValue >= 0
    }
    var expireAfterTimeUnit = TimeUnit.SECONDS
    var memorySize by Delegates.vetoable(1L) {
        _, _, newValue -> newValue >= 1
    }
}
