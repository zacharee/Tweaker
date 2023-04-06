package com.zacharee1.systemuituner.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.content.SharedPreferences
import android.os.Build
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.MalformedJsonException
import com.zacharee1.systemuituner.data.*
import kotlin.system.exitProcess

class PrefManager private constructor(context: Context) : ContextWrapper(context) {
    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: PrefManager? = null

        fun getInstance(context: Context): PrefManager {
            return instance ?: kotlin.run {
                val appContext = context.applicationContext ?: context

                PrefManager(appContext).also { instance = it }
            }
        }

        const val PERSISTENT_OPTIONS = "persistent_options"
        const val CUSTOM_PERSISTENT_OPTIONS = "custom_persistent_options"
        const val BLACKLISTED_ITEMS = "blacklisted_items"
        const val CUSTOM_BLACKLIST_ITEMS = "custom_blacklisted_items"
        const val SAVED_OPTIONS = "saved_options"
        const val FORCE_ENABLE_ALL = "force_enable_all"
        const val SAVED_FULL_IMMERSIVE_WHITELIST = "full_immersive_whitelist"
        const val SAVED_FULL_IMMERSIVE_BLACKLIST = "full_immersive_blacklist"
        const val SAVED_NAV_IMMERSIVE_WHITELIST = "nav_immersive_whitelist"
        const val SAVED_NAV_IMMERSIVE_BLACKLIST = "nav_immersive_blacklist"
        const val SAVED_STATUS_IMMERSIVE_WHITELIST = "status_immersive_whitelist"
        const val SAVED_STATUS_IMMERSIVE_BLACKLIST = "status_immersive_blacklist"
        const val SAW_SYSTEM_ALERT_WINDOW = "saw_system_alert_window"
        const val SAW_NOTIFICATIONS_ALERT = "saw_notifications_alert"
        const val ENABLE_CRASH_REPORTS = "enable_crash_reports"
        const val DID_INTRO = "did_intro"
    }

    /**
     * Should be in format type:key
     * @see [PersistentOption]
     */
    var persistentOptions: HashSet<PersistentOption>
        get() = HashSet(getStringSet(PERSISTENT_OPTIONS).mapNotNull {
            try {
                PersistentOption.fromString(it)
            } catch (e: MalformedJsonException) {
                null
            }
        })
        set(value) {
            putStringSet(PERSISTENT_OPTIONS, HashSet(value.map { it.toString() }))
        }

    var customPersistentOptions: HashSet<CustomPersistentOption>
        get() = HashSet(getStringSet(CUSTOM_PERSISTENT_OPTIONS).mapNotNull {
            try {
                CustomPersistentOption.fromString(it)
            } catch (e: MalformedJsonException) {
                null
            }
        })
        set(value) {
            putStringSet(CUSTOM_PERSISTENT_OPTIONS, HashSet(value.map { it.toString() }))
        }

    var blacklistedItems: HashSet<String>
        get() = HashSet(getStringSet(BLACKLISTED_ITEMS))
        set(value) {
            putStringSet(BLACKLISTED_ITEMS, HashSet(value))
        }

    var customBlacklistItems: HashSet<CustomBlacklistItemInfo>
        get() = HashSet(getStringSet(CUSTOM_BLACKLIST_ITEMS).mapNotNull {
            try {
                CustomBlacklistItemInfo.fromString(it)
            } catch (e: MalformedJsonException) {
                null
            }
        })
        set(value) {
            putStringSet(CUSTOM_BLACKLIST_ITEMS, HashSet(value.map { it.toString() }))
        }

    var savedOptions: HashSet<SavedOption>
        get() = HashSet(getStringSet(SAVED_OPTIONS).mapNotNull {
            try {
                SavedOption.fromString(it)
            } catch (e: MalformedJsonException) {
                null
            }
        })
        set(value) {
            putStringSet(SAVED_OPTIONS, HashSet(value.map { it.toString() }))
        }

    var forceEnableAll: Boolean
        get() = getBoolean(FORCE_ENABLE_ALL, false)
        set(value) {
            putBoolean(FORCE_ENABLE_ALL, value)
        }

    var sawSystemAlertWindow: Boolean
        get() = getBoolean(SAW_SYSTEM_ALERT_WINDOW, false)
        set(value) {
            putBoolean(SAW_SYSTEM_ALERT_WINDOW, value)
        }

    var sawNotificationsAlert: Boolean
        get() = getBoolean(SAW_NOTIFICATIONS_ALERT, false)
        set(value) {
            putBoolean(SAW_NOTIFICATIONS_ALERT, value)
        }

    var enableCrashReports: Boolean?
        get() = getString(ENABLE_CRASH_REPORTS, null)?.toBooleanStrictOrNull()
        set(value) {
            putString(ENABLE_CRASH_REPORTS, value?.toString())
        }

    var didIntro: Boolean
        get() = getBoolean(DID_INTRO, false)
        set(value) {
            putBoolean(DID_INTRO, value)
        }

    val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
    val gson: Gson = GsonBuilder()
        .create()

    fun findOption(type: SettingsType, key: String): SavedOption? {
        return savedOptions.find { it.key == key && it.type == type }
    }

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

    fun putImmersiveBlacklist(type: ImmersiveManager.ImmersiveMode, blacklist: ArrayList<String>) {
        putString(
            when (type) {
                ImmersiveManager.ImmersiveMode.FULL -> SAVED_FULL_IMMERSIVE_BLACKLIST
                ImmersiveManager.ImmersiveMode.NAV -> SAVED_NAV_IMMERSIVE_BLACKLIST
                ImmersiveManager.ImmersiveMode.STATUS -> SAVED_STATUS_IMMERSIVE_BLACKLIST
                else -> throw IllegalArgumentException("$type is not a valid immersive type")
            },
            gson.toJson(blacklist)
        )
    }

    fun getImmersiveBlacklist(type: ImmersiveManager.ImmersiveMode): ArrayList<String> {
        return gson.fromJson(
            getString(when (type) {
                ImmersiveManager.ImmersiveMode.FULL -> SAVED_FULL_IMMERSIVE_BLACKLIST
                ImmersiveManager.ImmersiveMode.NAV -> SAVED_NAV_IMMERSIVE_BLACKLIST
                ImmersiveManager.ImmersiveMode.STATUS -> SAVED_STATUS_IMMERSIVE_BLACKLIST
                else -> throw IllegalArgumentException("$type is not a valid immersive type")
            }),
            object : TypeToken<ArrayList<String>>() {}.type
        ) ?: ArrayList()
    }

    fun putImmersiveWhitelist(type: ImmersiveManager.ImmersiveMode, whitelist: ArrayList<String>) {
        putString(
            when (type) {
                ImmersiveManager.ImmersiveMode.FULL -> SAVED_FULL_IMMERSIVE_WHITELIST
                ImmersiveManager.ImmersiveMode.NAV -> SAVED_NAV_IMMERSIVE_WHITELIST
                ImmersiveManager.ImmersiveMode.STATUS -> SAVED_STATUS_IMMERSIVE_WHITELIST
                else -> throw IllegalArgumentException("$type is not a valid immersive type")
            },
            gson.toJson(whitelist)
        )
    }

    fun getImmersiveWhitelist(type: ImmersiveManager.ImmersiveMode): ArrayList<String> {
        return gson.fromJson(
            getString(when (type) {
                ImmersiveManager.ImmersiveMode.FULL -> SAVED_FULL_IMMERSIVE_WHITELIST
                ImmersiveManager.ImmersiveMode.NAV -> SAVED_NAV_IMMERSIVE_WHITELIST
                ImmersiveManager.ImmersiveMode.STATUS -> SAVED_STATUS_IMMERSIVE_WHITELIST
                else -> throw IllegalArgumentException("$type is not a valid immersive type")
            }),
            object : TypeToken<ArrayList<String>>() {}.type
        ) ?: ArrayList()
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