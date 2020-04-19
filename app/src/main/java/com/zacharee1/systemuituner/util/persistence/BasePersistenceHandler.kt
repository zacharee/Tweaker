package com.zacharee1.systemuituner.util.persistence

import android.content.Context
import com.zacharee1.systemuituner.util.SettingsType

abstract class BasePersistenceHandler<PreferenceValueType>(internal val context: Context) {
    abstract val settingsType: SettingsType
    abstract val settingsKey: String

    abstract fun areValuesTheSame(preferenceValue: PreferenceValueType?, settingsValue: String?): Boolean
    abstract fun getPreferenceValue(): PreferenceValueType?
    abstract fun getSettingsValue(): String?
    abstract fun getPreferenceValueAsString(): String?

    fun compareValues(): Boolean {
        return areValuesTheSame(getPreferenceValue(), getSettingsValue())
    }
}