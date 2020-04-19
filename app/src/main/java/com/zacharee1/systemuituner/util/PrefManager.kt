package com.zacharee1.systemuituner.util

import android.content.Context
import android.content.ContextWrapper
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.zacharee1.systemuituner.data.CustomBlacklistItemInfo

class PrefManager private constructor(context: Context) : ContextWrapper(context) {
    companion object {
        private var instance: PrefManager? = null

        fun getInstance(context: Context): PrefManager {
            return instance ?: kotlin.run {
                PrefManager(context.applicationContext).also { instance = it }
            }
        }

        const val PERSISTENT_OPTIONS = "persistent_options"
        const val BLACKLISTED_ITEMS = "blacklisted_items"
        const val CUSTOM_BLACKLIST_ITEMS = "custom_blacklist_items"
    }

    /**
     * Should be in format type:key
     * @see [PersistentOption]
     */
    var persistentOptions: HashSet<PersistentOption>
        get() = HashSet(getStringSet(PERSISTENT_OPTIONS).map { PersistentOption.fromString(it) })
        set(value) {
            putStringSet(PERSISTENT_OPTIONS, HashSet(value.map { it.toString() }))
        }

    var blacklistedItems: HashSet<String>
        get() = HashSet(getStringSet(BLACKLISTED_ITEMS))
        set(value) {
            putStringSet(BLACKLISTED_ITEMS, HashSet(value))
        }

    var customBlacklistItems: HashSet<CustomBlacklistItemInfo>
        get() = HashSet(getStringSet(CUSTOM_BLACKLIST_ITEMS).map { CustomBlacklistItemInfo.fromString(it) })
        set(value) {
            putStringSet(CUSTOM_BLACKLIST_ITEMS, HashSet(value.map { it.toString() }))
        }

    val prefs = PreferenceManager.getDefaultSharedPreferences(this)

    fun getString(key: String, def: String? = null): String? = prefs.getString(key, def)
    fun getInt(key: String, def: Int = 0) = prefs.getInt(key, def)
    fun getFloat(key: String, def: Float = 0f) = prefs.getFloat(key, def)
    fun getLong(key: String, def: Long = 0L) = prefs.getLong(key, def)
    fun getBoolean(key: String, def: Boolean = false) = prefs.getBoolean(key, def)
    fun getStringSet(key: String): Set<String> = prefs.getStringSet(key, HashSet<String>())

    fun putString(key: String, value: String?) = prefs.edit().putString(key, value).apply()
    fun putInt(key: String, value: Int) = prefs.edit().putInt(key, value).apply()
    fun putFloat(key: String, value: Float) = prefs.edit().putFloat(key, value).apply()
    fun putLong(key: String, value: Long) = prefs.edit().putLong(key, value).apply()
    fun putBoolean(key: String, value: Boolean) = prefs.edit().putBoolean(key, value).apply()
    fun putStringSet(key: String, value: Set<String>) = prefs.edit().putStringSet(key, value).apply()

    fun reset() = prefs.edit { clear() }
}