package com.zacharee1.systemuituner.util.persistence

import android.content.Context
import com.zacharee1.systemuituner.data.SettingsType
import com.zacharee1.systemuituner.util.prefManager
import com.zacharee1.systemuituner.util.writeSetting

class BlacklistPersistenceHandler(context: Context) : BasePersistenceHandler<HashSet<String>>(context) {
    override val settingsKey: String = "icon_blacklist"
    override val settingsType: SettingsType = SettingsType.SECURE

    override fun getPreferenceValue(): HashSet<String> {
        return context.prefManager.blacklistedItems
    }

    override fun getPreferenceValueAsString(): String {
        return getPreferenceValue().joinToString(",")
    }

    override fun savePreferenceValue(value: String?) {
        if (value == null) {
            context.prefManager.blacklistedItems = HashSet()
        } else {
            context.prefManager.blacklistedItems = HashSet(value.split(","))
        }
    }

    override fun areValuesTheSame(preferenceValue: HashSet<String>?, settingsValue: String?): Boolean {
        if (preferenceValue.isNullOrEmpty()) return settingsValue.isNullOrBlank()
        if (settingsValue.isNullOrBlank()) return preferenceValue.isEmpty()

        val split = settingsValue.split(",")
        return split.size == preferenceValue.size
                && preferenceValue.containsAll(split)
                && split.containsAll(preferenceValue)
    }

    override suspend fun doInitialSet() {
        val prefValue = getPreferenceValue()
        context.prefManager.saveOption(settingsType, settingsKey, null)
        context.writeSetting(SettingsType.SECURE, settingsKey, null)
        context.prefManager.saveOption(settingsType, settingsKey, prefValue)
    }
}