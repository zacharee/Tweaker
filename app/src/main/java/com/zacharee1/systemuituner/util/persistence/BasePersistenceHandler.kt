package com.zacharee1.systemuituner.util.persistence

import android.content.Context
import com.zacharee1.systemuituner.data.SettingsType
import com.zacharee1.systemuituner.util.getSetting

abstract class BasePersistenceHandler<PreferenceValueType : Any>(internal val context: Context) {
    abstract val settingsType: SettingsType
    abstract val settingsKey: String

    abstract fun areValuesTheSame(preferenceValue: PreferenceValueType?, settingsValue: String?): Boolean
    abstract fun getPreferenceValue(): PreferenceValueType?
    abstract fun getPreferenceValueAsString(): String?
    abstract fun savePreferenceValue(value: String?)

    open fun doInitialSet() {}

    fun getSettingsValue(): String? {
        return context.getSetting(settingsType, settingsKey)
    }

    fun compareValues(): Boolean {
        return areValuesTheSame(getPreferenceValue(), getSettingsValue())
    }
}