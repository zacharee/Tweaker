package com.rw.tweaks.util

import android.content.Context
import android.content.ContextWrapper
import androidx.preference.PreferenceManager

class PrefManager private constructor(context: Context) : ContextWrapper(context) {
    companion object {
        const val KEY_CHECKED_FULL_IMMERSIVE_APPS = "checked_full_immersive_apps"
        const val KEY_CHECKED_STATUS_IMMERSIVE_APPS = "checked_status_immersive_apps"
        const val KEY_CHECKED_NAV_IMMERSIVE_APPS = "checked_nav_immersive_apps"
        const val KEY_CHECKED_BLACKLISTED_IMMERSIVE_APPS = "checked_blacklisted_immersive_apps"

        private var instance: PrefManager? = null

        fun getInstance(context: Context): PrefManager {
            return instance ?: kotlin.run {
                PrefManager(context.applicationContext).also { instance = it }
            }
        }
    }

    var checkedFullImmersiveApps: ArrayList<String>
        get() = ArrayList(getStringSet(KEY_CHECKED_FULL_IMMERSIVE_APPS))
        set(value) {
            putStringSet(KEY_CHECKED_FULL_IMMERSIVE_APPS, HashSet(value))
        }

    var checkedStatusImmersiveApps: ArrayList<String>
        get() = ArrayList(getStringSet(KEY_CHECKED_STATUS_IMMERSIVE_APPS))
        set(value) {
            putStringSet(KEY_CHECKED_STATUS_IMMERSIVE_APPS, HashSet(value))
        }

    var checkedNavImmersiveApps: ArrayList<String>
        get() = ArrayList(getStringSet(KEY_CHECKED_NAV_IMMERSIVE_APPS))
        set(value) {
            putStringSet(KEY_CHECKED_NAV_IMMERSIVE_APPS, HashSet(value))
        }

    var checkedBlacklistedImmersiveApps: ArrayList<String>
        get() = ArrayList(getStringSet(KEY_CHECKED_BLACKLISTED_IMMERSIVE_APPS))
        set(value) {
            putStringSet(KEY_CHECKED_BLACKLISTED_IMMERSIVE_APPS, HashSet(value))
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
}