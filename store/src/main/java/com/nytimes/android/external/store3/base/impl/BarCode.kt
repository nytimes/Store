package com.nytimes.android.external.store3.base.impl

import com.nytimes.android.external.store3.base.Persister
import java.io.Serializable

/**
 * [Barcode][BarCode] is used as a unique
 * identifier for a particular [Store]
 *
 *
 * Barcode will be passed to   Fetcher
 * and [Persister]
 */
data class BarCode(val type: String, val key: String) : Serializable {
    companion object {
        private val EMPTY_BARCODE = BarCode("", "")

        fun empty(): BarCode {
            return EMPTY_BARCODE
        }
    }
}
