package com.zacharee1.systemuituner.data

import androidx.preference.Preference
import com.zacharee1.systemuituner.interfaces.IDangerousPreference
import java.util.*

data class PreferenceHolder(
    val className: String?,
    val widgetLayoutResource: Int,
    val layoutResource: Int,
    val isEnabled: Boolean,
    val isDangerous: Boolean
) {
    constructor(preference: Preference) : this(
        preference::class.java.canonicalName,
        preference.widgetLayoutResource,
        preference.layoutResource,
        preference.isEnabled,
        (preference as? IDangerousPreference)?.dangerous ?: false
    )
}