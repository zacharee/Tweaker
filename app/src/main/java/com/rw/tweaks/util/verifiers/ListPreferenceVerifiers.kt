package com.rw.tweaks.util.verifiers

import android.content.Context
import com.rw.tweaks.util.hasSdCard

abstract class BaseListPreferenceVerifier(internal val context: Context) {
    abstract fun verifyEntries(
        entries: Array<out CharSequence>,
        values: Array<out CharSequence>
    ): Pair<Array<out CharSequence>, Array<out CharSequence>>
}

class StorageVerifier(context: Context) : BaseListPreferenceVerifier(context) {
    override fun verifyEntries(
        entries: Array<out CharSequence>,
        values: Array<out CharSequence>
    ): Pair<Array<CharSequence>, Array<CharSequence>> {
        val entryList = ArrayList(entries.toList())
        val valueList = ArrayList(values.toList())

        if (!context.hasSdCard) {
            entryList.removeAt(entryList.lastIndex)
            valueList.removeAt(valueList.lastIndex)
        }

        return entryList.toTypedArray() to valueList.toTypedArray()
    }
}