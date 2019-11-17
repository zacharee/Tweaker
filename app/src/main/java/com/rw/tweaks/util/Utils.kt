package com.rw.tweaks.util

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

enum class SettingsType(val value: Int) {
    UNDEFINED(-1),
    GLOBAL(0),
    SECURE(1),
    SYSTEM(2),
}

val mainHandler = Handler(Looper.getMainLooper())

val Context.prefManager: PrefManager
    get() = PrefManager.getInstance(this)

val Context.hasSdCard: Boolean
    get() = ContextCompat.getExternalFilesDirs(this, null).size >= 2

fun Context.writeSetting(type: SettingsType, key: String?, value: Any?) {
    when (type) {
        SettingsType.GLOBAL -> writeGlobal(key, value)
        SettingsType.SECURE -> writeSecure(key, value)
        SettingsType.SYSTEM -> writeSystem(key, value)
        SettingsType.UNDEFINED -> throw IllegalStateException("SettingsType should not be undefined")
    }
}

fun Context.getSetting(type: SettingsType, key: String?): String? {
    return when (type) {
        SettingsType.GLOBAL -> Settings.Global.getString(contentResolver, key)
        SettingsType.SECURE -> Settings.Secure.getString(contentResolver, key)
        SettingsType.SYSTEM -> Settings.System.getString(contentResolver, key)
        SettingsType.UNDEFINED -> throw IllegalStateException("SettingsType should not be undefined")
    }
}

fun Context.resetAll() {
    try {
        Settings.Global.resetToDefaults(contentResolver, "tweaker")
    } catch (e: SecurityException) {}

    try {
        Settings.Secure.resetToDefaults(contentResolver, "tweaker")
    } catch (e: SecurityException) {}

    //There doesn't seem to be a reset option for Settings.System
}

fun Context.writeGlobal(key: String?, value: Any?) {
    try {
        Settings.Global.putString(contentResolver, key, value?.toString())
    } catch (e: SecurityException) {
        //TODO: Handle this
    }
}

fun Context.writeSecure(key: String?, value: Any?) {
    try {
        Settings.Secure.putString(contentResolver, key, value?.toString())
    } catch (e: SecurityException) {
        //TODO: Handle this
    }
}

fun Context.writeSystem(key: String?, value: Any?) {
    try {
        Settings.System.putString(contentResolver, key, value?.toString())
    } catch (e: SecurityException) {
        //TODO: Handle this
    }
}

fun Fragment.updateTitle(title: Int) {
    activity?.setTitle(title)
}

fun Fragment.updateTitle(title: CharSequence?) {
    activity?.title = title
}