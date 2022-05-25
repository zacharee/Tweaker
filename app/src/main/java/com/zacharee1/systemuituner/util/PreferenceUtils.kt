package com.zacharee1.systemuituner.util

import android.content.Context
import androidx.preference.*

val Context.prefManager: PrefManager
    get() = PrefManager.getInstance(this)

val Preference.defaultValue: Any?
    get() {
        return Preference::class.java
            .getDeclaredField("mDefaultValue")
            .apply { isAccessible = true }
            .get(this)
    }

fun PreferenceGroup.hasPreference(key: String): Boolean {
    forEach { child ->
        if (key == child.key) return@hasPreference true
    }

    return false
}

fun PreferenceGroup.indexOf(preference: Preference): Int {
    forEachIndexed { index, child ->
        if (child == preference) return index
    }

    return -1
}

fun PreferenceGroupAdapter.updatePreferences() {
    PreferenceGroupAdapter::class.java
        .getDeclaredMethod("updatePreferences")
        .apply { isAccessible = true }
        .invoke(this)
}