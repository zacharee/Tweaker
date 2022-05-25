package com.zacharee1.systemuituner.util

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.topjohnwu.superuser.Shell
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.data.SettingsType
import com.zacharee1.systemuituner.data.WriteSystemAddOnValues
import rikka.shizuku.Shizuku
import java.util.HashSet

fun Context.writeSetting(type: SettingsType, key: String?, value: Any?): Boolean {
    return when (type) {
        SettingsType.GLOBAL -> writeGlobal(key, value)
        SettingsType.SECURE -> writeSecure(key, value)
        SettingsType.SYSTEM -> writeSystem(key, value)
        SettingsType.UNDEFINED -> throw IllegalStateException("SettingsType should not be undefined")
    }
}

fun Context.getSetting(type: SettingsType, key: String?, def: Any? = null): String? {
    return try {
        when (type) {
            SettingsType.GLOBAL -> Settings.Global.getString(contentResolver, key)
            SettingsType.SECURE -> Settings.Secure.getString(contentResolver, key)
            SettingsType.SYSTEM -> Settings.System.getString(contentResolver, key)
            SettingsType.UNDEFINED -> throw IllegalStateException("SettingsType should not be undefined")
        }.orEmpty().ifBlank { def?.toString() }
    } catch (e: SecurityException) {
        if (Shizuku.pingBinder() && hasShizukuPermission) {
            Shizuku.newProcess(
                arrayOf("settings", "get", type.toString(), key),
                null,
                null
            ).run {
                inputStream.bufferedReader().use { it.readLine() }
            }
        } else {
            prefManager.savedOptions.find { it.key == key && it.type == type }?.value
        }
    }
}

fun Context.resetAll() {
    prefManager.reset()

    try {
        Settings.Global.resetToDefaults(contentResolver, null)
    } catch (_: SecurityException) {
    } catch (e: NoSuchMethodError) {
        Toast.makeText(this, e.localizedMessage, Toast.LENGTH_SHORT).show()
    }

    try {
        Settings.Secure.resetToDefaults(contentResolver, null)
    } catch (_: SecurityException) {
    } catch (e: NoSuchMethodError) {
        Toast.makeText(this, e.localizedMessage, Toast.LENGTH_SHORT).show()
    }

    //There doesn't seem to be a reset option for Settings.System
}

fun Context.writeGlobal(key: String?, value: Any?): Boolean {
    if (key.isNullOrBlank()) return false
    return try {
        Settings.Global.putString(contentResolver, key, value?.toString())
        true
    } catch (e: SecurityException) {
        Log.e("SystemUI Tuner", "Failed to write to Global", e)
        false
    }
}

fun Context.writeSecure(key: String?, value: Any?): Boolean {
    if (key.isNullOrBlank()) return false
    return try {
        Settings.Secure.putString(contentResolver, key, value?.toString())
        true
    } catch (e: SecurityException) {
        Log.e("SystemUI Tuner", "Failed to write to Secure", e)
        false
    }
}

fun Context.writeSystem(key: String?, value: Any?): Boolean {
    if (key.isNullOrBlank()) return false
    fun onFail(e: Exception): Boolean {
        return when {
            Shell.rootAccess() -> {
                Shell.su("content insert --uri content://settings/system --bind name:s:$key --bind value:s:$value --bind package:s:$packageName")
                    .exec()
                true
            }
            isWriteSystemAddOnInstalled() -> {
                writeSystemSettingsWithAddOnNoResult(key, value)
                true
            }
            Shizuku.pingBinder() && hasShizukuPermission -> {
                Shizuku.newProcess(
                    arrayOf(
                        "content", "insert",
                        "--uri content://settings/system",
                        "--bind name:s:$key",
                        "--bind value:s:$value",
                        "--bind package:s:$packageName"
                    ),
                    null, null
                ).waitFor() == 0
            }
            else -> {
                Log.e("SystemUI Tuner", "Failed to write to System", e)
                false
            }
        }
    }

    return try {
        Settings.System.putString(contentResolver, key, value?.toString())
        true
    } catch (e: SecurityException) {
        onFail(e)
    } catch (e: IllegalArgumentException) {
        onFail(e)
    } catch (e: NullPointerException) {
        onFail(e)
    }
}

fun Context.buildNonResettablePreferences(): Set<String> {
    val names = HashSet<String>()
    try {
        val cursor = contentResolver.query(
            Uri.parse("content://settings/system"),
            arrayOf("name", "package"),
            null,
            null,
            null
        )
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val pkg = cursor.getString(1)
                if (pkg == packageName || pkg == "tk.zwander.systemuituner.systemsettings" || pkg == "com.android.shell") {
                    names.add("${resources.getString(R.string.system)}: ${cursor.getString(0)}")
                }
            }
            cursor.close()
        }
    } catch (_: IllegalArgumentException) {
    }
    names.addAll(prefManager.savedOptions.filter { it.type == SettingsType.SYSTEM }.map { "${resources.getString(
        R.string.system)}: ${it.key}" })
    return names
}

fun Context.writeSystemSettingsWithAddOnNoResult(key: String?, value: Any?) {
    val intent = Intent(WriteSystemAddOnValues.ACTION_WRITE_SYSTEM)
    intent.putExtra(WriteSystemAddOnValues.EXTRA_KEY, key)
    intent.putExtra(WriteSystemAddOnValues.EXTRA_VALUE, value?.toString())
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    intent.setClassName(
        "tk.zwander.systemuituner.systemsettings",
        "tk.zwander.systemuituner.systemsettings.WriteSystemActivity"
    )

    try {
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Log.e("SystemUITuner", "Add-on error", e)
    }
}

fun Activity.writeSystemSettingsWithAddOnResult(key: String?, value: Any?) {
    val intent = Intent(WriteSystemAddOnValues.ACTION_WRITE_SYSTEM)
    intent.putExtra(WriteSystemAddOnValues.EXTRA_KEY, key)
    intent.putExtra(WriteSystemAddOnValues.EXTRA_VALUE, value?.toString())
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    intent.setClassName(
        "tk.zwander.systemuituner.systemsettings",
        "tk.zwander.systemuituner.systemsettings.WriteSystemActivity"
    )

    try {
        startActivityForResult(intent, WriteSystemAddOnValues.WRITE_SYSTEM_REQUEST_CODE)
    } catch (e: ActivityNotFoundException) {
        Log.e("SystemUITuner", "Add-on error", e)
    }
}

fun Fragment.writeSystemSettingsWithAddOnResult(key: String?, value: Any?) {
    requireActivity().writeSystemSettingsWithAddOnResult(key, value)
}

fun Context.isWriteSystemAddOnInstalled(): Boolean {
    return try {
        packageManager.getPackageInfo("tk.zwander.systemuituner.systemsettings", 0)
        true
    } catch (e: Exception) {
        false
    }
}