package com.zacharee1.systemuituner.util.verifiers

import android.content.Context
import com.zacharee1.systemuituner.util.hasSdCard

abstract class BaseListPreferenceVerifier(internal val context: Context) {
    abstract fun verifyEntries(
        entries: Array<CharSequence?>?,
        values: Array<CharSequence?>?
    ): Pair<Array<CharSequence?>?, Array<CharSequence?>?>
}

class StorageVerifier(context: Context) : BaseListPreferenceVerifier(context) {
    override fun verifyEntries(
        entries: Array<CharSequence?>?,
        values: Array<CharSequence?>?
    ): Pair<Array<CharSequence?>?, Array<CharSequence?>?> {
        if (entries == null || values == null) return Pair<Array<CharSequence?>?, Array<CharSequence?>?>(null, null)

        val entryList = ArrayList(entries.toList())
        val valueList = ArrayList(values.toList())

        if (!context.hasSdCard) {
            entryList.removeAt(entryList.lastIndex)
            valueList.removeAt(valueList.lastIndex)
        }

        return entryList.toTypedArray() to valueList.toTypedArray()
    }
}