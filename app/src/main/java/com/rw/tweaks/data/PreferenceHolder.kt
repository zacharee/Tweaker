package com.rw.tweaks.data

import androidx.preference.Preference

data class PreferenceHolder(
    val className: String?,
    val widgetLayoutResource: Int,
    val layoutResource: Int,
    val isEnabled: Boolean
) {
    constructor(preference: Preference) : this(
        preference::class.java.canonicalName,
        preference.widgetLayoutResource,
        preference.layoutResource,
        preference.isEnabled
    )
    override fun equals(other: Any?): Boolean {
        return other is PreferenceHolder
                && other.className == className
                && other.widgetLayoutResource == widgetLayoutResource
                && other.layoutResource == layoutResource
                && other.isEnabled == isEnabled
    }
}