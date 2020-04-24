package com.zacharee1.systemuituner.util

import android.content.Context
import android.content.ContextWrapper
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.zacharee1.systemuituner.data.CustomBlacklistItemInfo
import com.zacharee1.systemuituner.data.CustomPersistentOption
import com.zacharee1.systemuituner.data.PersistentOption
import com.zacharee1.systemuituner.data.SavedOption
import kotlin.ClassCastException

class PrefManager private constructor(context: Context) : ContextWrapper(context) {
    companion object {
        private var instance: PrefManager? = null

        fun getInstance(context: Context): PrefManager {
            return instance ?: kotlin.run {
                PrefManager(context.applicationContext).also { instance = it }
            }
        }

        const val PERSISTENT_OPTIONS = "persistent_options"
        const val CUSTOM_PERSISTENT_OPTIONS = "custom_persistent_options"
        const val BLACKLISTED_ITEMS = "blacklisted_items"
        const val CUSTOM_BLACKLIST_ITEMS = "custom_blacklist_items"
        const val SAVED_OPTIONS = "saved_options"
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

    var customPersistentOptions: HashSet<CustomPersistentOption>
        get() = HashSet(getStringSet(CUSTOM_PERSISTENT_OPTIONS).map { CustomPersistentOption.fromString(it) })
        set(value) {
            putStringSet(CUSTOM_PERSISTENT_OPTIONS, HashSet(value.map { it.toString() }))
        }

    var blacklistedItems: HashSet<String>
        get() = HashSet(getStringSet(BLACKLISTED_ITEMS))
        set(value) {
            putStringSet(BLACKLISTED_ITEMS, HashSet(value))
        }

    var customBlacklistItems: HashSet<CustomBlacklistItemInfo>
        get() = try {
            HashSet(getStringSet(CUSTOM_BLACKLIST_ITEMS).map { CustomBlacklistItemInfo.fromString(it) })
        } catch (e: ClassCastException) {
            //This is a bit of a hack, since I accidentally used the same key as a preference that was a String before this redesign
            prefs.edit {
                remove(CUSTOM_BLACKLIST_ITEMS)
            }
            HashSet()
        }
        set(value) {
            putStringSet(CUSTOM_BLACKLIST_ITEMS, HashSet(value.map { it.toString() }))
        }

    var savedOptions: HashSet<SavedOption>
        get() = HashSet(getStringSet(SAVED_OPTIONS).map { SavedOption.fromString(it) })
        set(value) {
            putStringSet(SAVED_OPTIONS, HashSet(value.map { it.toString() }))
        }

    val prefs = PreferenceManager.getDefaultSharedPreferences(this)

    fun saveOption(type: SettingsType, key: String, value: Any?) {
        val options = savedOptions
        val found = options.find { it.type == type && it.key == key }

        if (found != null) {
            found.value = value?.toString()
        } else {
            options.add(SavedOption(type, key, value?.toString()))
        }

        savedOptions = options
    }

    fun removeOption(type: SettingsType, key: String) {
        savedOptions = savedOptions.apply {
            removeAll { it.type == type && it.key == key }
        }
    }

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