package com.zacharee1.systemuituner.interfaces

interface IListPreference : IDialogPreference {
    val entries: Array<CharSequence?>?
    val entryValues: Array<CharSequence?>?
    var value: String?

    fun findIndexOfValue(value: String?): Int {
        if (value != null && entryValues != null) {
            for (i in entryValues!!.indices.reversed()) {
                if (entryValues!![i] == value) {
                    return i
                }
            }
        }
        return -1
    }

    fun callChangeListener(newValue: Any?): Boolean
}