package com.zacharee1.systemuituner.util

import android.content.Context
import android.net.Uri
import android.os.CountDownTimer
import android.os.SystemProperties
import android.provider.Settings
import android.util.Log
import com.bugsnag.android.BreadcrumbType
import com.topjohnwu.superuser.Shell
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.activities.ReadSettingFailActivity
import com.zacharee1.systemuituner.activities.WriteSettingFailActivity
import com.zacharee1.systemuituner.data.SettingsType
import com.zacharee1.systemuituner.dialogs.RoundedBottomSheetDialog
import com.zacharee1.systemuituner.systemsettingsaddon.library.settingsAddon
import com.zacharee1.systemuituner.views.LockscreenShortcuts
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import rikka.shizuku.Shizuku

data class SettingsInfo(
    val type: SettingsType,
    val key: String?,
    val value: Any?
)

private suspend fun Context.revertDialog(
    vararg data: Pair<SettingsInfo, String?>,
    saveOption: Boolean
) {
    withContext(Dispatchers.Main) {
        activityContext?.apply {
            val originalValues = data.map { it to it.second }

            if (originalValues.all { o -> o.first.first.value.toString() == o.second.toString() }) {
                // No changes, no confirmation
                return@withContext
            }

            val timeoutMs = 10_000L

            var remainder = timeoutMs

            val dialog = RoundedBottomSheetDialog(this)
                .apply {
                    setTitle(R.string.setting_applied_dialog)
                    setMessage(
                        resources.getString(
                            R.string.setting_applied_dialog_desc,
                            (remainder / 1000).toString()
                        )
                    )
                    setCancelable(false)
                }

            val timer = object : CountDownTimer(timeoutMs, 1000L) {
                override fun onFinish() {
                    runBlocking {
                        originalValues.forEach { (info, setting) ->
                            writeSetting(info.first.type, info.first.key, setting, false, saveOption)
                        }
                    }
                    try {
                        dialog.dismiss()
                    } catch (_: IllegalArgumentException) {}
                }

                override fun onTick(millisUntilFinished: Long) {
                    remainder = millisUntilFinished
                    dialog.setMessage(
                        resources.getString(
                            R.string.setting_applied_dialog_desc,
                            (remainder / 1000).toString()
                        )
                    )
                }
            }

            dialog.setPositiveButton(R.string.keep) { _, _ ->
                timer.cancel()
                dialog.dismiss()
            }
            dialog.setNegativeButton(R.string.revert) { _, _ ->
                timer.cancel()
                timer.onFinish()
                dialog.dismiss()
            }

            dialog.show()
            timer.start()
        }
    }
}

suspend fun Context.writeSettingsBulk(
    vararg data: SettingsInfo,
    revertable: Boolean = false,
    saveOption: Boolean = false
): Boolean {
    val mapped = data.map { it to getSetting(it.type, it.key) }

    val success = data.all { (type, key, value) ->
        writeSetting(type, key, value, false, saveOption)
    }

    if (success && revertable) {
        revertDialog(*mapped.toTypedArray(), saveOption = saveOption)
    }

    return success
}

suspend fun Context.writeSetting(
    type: SettingsType,
    key: String?,
    value: Any?,
    revertable: Boolean = false,
    saveOption: Boolean = true,
): Boolean {
    val revertInfo = SettingsInfo(type, key, value) to getSetting(type, key)

    BugsnagUtils.leaveBreadcrumb("Attempting to write setting ${type}, ${key}, ${value}. Revertable $revertable, save $saveOption.")

    return withContext(Dispatchers.IO) {
        val success = when (type) {
            SettingsType.GLOBAL -> writeGlobal(key, value)
            SettingsType.SECURE -> writeSecure(key, value)
            SettingsType.SYSTEM -> writeSystem(key, value)
            SettingsType.UNDEFINED -> throw IllegalStateException("SettingsType should not be undefined")
        }

        if (success) {
            if (saveOption && key != null) {
                val handler = PersistenceHandlerRegistry.handlers.find { it.settingsKey == key }
                if (handler != null) {
                    handler.savePreferenceValue(value?.toString())
                } else {
                    prefManager.saveOption(type, key, value)
                }
            }

            if (revertable) {
                revertDialog(revertInfo, saveOption = saveOption)
            }
        }

        success
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
        BugsnagUtils.notify(IllegalStateException("Unable to read setting ${type}, ${key}, ${def}.", e))
        when {
            Shizuku.pingBinder() -> {
                if (hasShizukuPermission) {
                    shizukuServiceManager.waitForService()
                        .run {
                            try {
                                when (type) {
                                    SettingsType.GLOBAL -> this.readGlobal(key)
                                    SettingsType.SECURE -> this.readSecure(key)
                                    SettingsType.SYSTEM -> this.readSystem(key)
                                    else -> null
                                }
                            } catch (e: Throwable) {
                                BugsnagUtils.notify(IllegalStateException("Failed to read setting through Shizuku.", e))
                                null
                            }
                        }
                } else {
                    BugsnagUtils.leaveBreadcrumb("No Shizuku permission but it is installed. Requesting permission.")
                    Shizuku.requestPermission(100)
                    null
                }
            }
            settingsAddon.hasService -> {
                settingsAddon.binder?.readSetting(
                    type.toLibraryType(),
                    key
                )
            }
            else -> {
                ReadSettingFailActivity.start(this, type, key)
                null
            }
        }
    }
}

fun Context.resetAll() {
    prefManager.reset()

    try {
        Settings.Global.resetToDefaults(contentResolver, null)
    } catch (e: Throwable) {
        BugsnagUtils.notify(e)
    }

    try {
        Settings.Secure.resetToDefaults(contentResolver, null)
    } catch (e: Throwable) {
        BugsnagUtils.notify(e)
    }

    //There doesn't seem to be a reset option for Settings.System
}

private fun Context.writeGlobal(key: String?, value: Any?): Boolean {
    if (key.isNullOrBlank()) return false
    return try {
        Settings.Global.putString(contentResolver, key, value?.toString())
        true
    } catch (e: SecurityException) {
        Log.e("SystemUITuner", "Failed to write to Global", e)
        BugsnagUtils.notify(e)
        WriteSettingFailActivity.start(this, SettingsType.GLOBAL, key, value)
        false
    }
}

private fun Context.writeSecure(key: String?, value: Any?): Boolean {
    if (key.isNullOrBlank()) return false
    return try {
        Settings.Secure.putString(contentResolver, key, value?.toString())
        true
    } catch (e: SecurityException) {
        BugsnagUtils.notify(e)
        Log.e("SystemUITuner", "Failed to write to Secure", e)
        WriteSettingFailActivity.start(this, SettingsType.SECURE, key, value)
        false
    }
}

private fun Context.writeSystem(key: String?, value: Any?): Boolean {
    if (key.isNullOrBlank()) return false
    fun onFail(e: Exception): Boolean? {
        BugsnagUtils.leaveBreadcrumb("Failed to write to Settings.System with Exception", mapOf("stacktrace" to e.stackTraceToString()), BreadcrumbType.ERROR)

        return when {
            Shell.rootAccess() -> {
                Shell.su("content insert --uri content://settings/system --bind name:s:$key --bind value:s:$value --bind package:s:$packageName")
                    .exec()
                true
            }
            Shizuku.pingBinder() -> {
                if (hasShizukuPermission) {
                    try {
                        shizukuServiceManager.waitForService()
                            .writeSystem(key, value?.toString(), packageName)
                    } catch (e: Throwable) {
                        BugsnagUtils.notify(IllegalStateException("Unable to write to Settings.System using Shizuku.", e))
                        Log.e("SystemUITuner", "Failed to write to System $key $value", e)
                        false
                    }
                } else {
                    BugsnagUtils.leaveBreadcrumb("No Shizuku permission but it is installed. Requesting permission.")
                    Shizuku.requestPermission(100)
                    null
                }
            }
            settingsAddon.hasService && settingsAddon.binderAvailable -> {
                val result = settingsAddon.binder?.writeSetting(
                    com.zacharee1.systemuituner.systemsettingsaddon.library.SettingsType.SYSTEM,
                    key,
                    value?.toString()
                )

                if (result == true) {
                    true
                } else {
                    null
                }
            }
            else -> {
                BugsnagUtils.notify(e)
                Log.e("SystemUITuner", "Failed to write to System $key $value", e)
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
    }.let {
        if (it == false) {
            WriteSettingFailActivity.start(this, SettingsType.SYSTEM, key, value)
        }

        it ?: false
    }
}

fun Context.getDefaultForSetting(type: SettingsType, key: String): String? {
    val possibleDefaults = arrayListOf<String>()

    try {
        val cursor = contentResolver.query(
            Uri.parse("content://settings/${type.name.lowercase()}"),
            arrayOf("name", "defaultValue"),
            null,
            null,
            null
        )

        if (cursor != null) {
            while (cursor.moveToNext()) {
                val default = cursor.getString(1)
                val name = cursor.getString(0)
                if (!default.isNullOrBlank() && name == key) possibleDefaults.add(default)
            }

            cursor.close()
        }
    } catch (e: Exception) {
        Log.e("SystemUITuner", "Error", e)
    }

    Log.e("SystemUITuner", "possible defaults $possibleDefaults")

    return if (possibleDefaults.isEmpty()) null else possibleDefaults[0]
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
    } catch (e: IllegalArgumentException) {
        Log.e("SystemUITuner", "Error", e)
    }
    names.addAll(prefManager.savedOptions.filter { it.type == SettingsType.SYSTEM }.map {
        "${
            resources.getString(
                R.string.system
            )
        }: ${it.key}"
    })
    return names
}

fun Context.buildDefaultSamsungLockScreenShortcuts(): String {
    val isTablet = SystemProperties.get("ro.build.characteristics").contains("tablet")
    val hasSPen = packageManager.hasSystemFeature("com.sec.feature.spen_usp")
    val isNotesInstalled = try {
        packageManager.getApplicationInfoCompat("com.samsung.android.app.notes")
        true
    } catch (e: Exception) {
        false
    }
    val isSBrowserInstalled = try {
        packageManager.getApplicationInfoCompat("com.sec.android.app.sbrowser")
        true
    } catch (e: Exception) {
        false
    }
    val cscDefault = try {
        Class.forName("com.samsung.android.feature.SemCscFeature").run {
            val instance = this.getDeclaredMethod("getInstance").invoke(null)
            getMethod("getString", String::class.java)
                .invoke(instance, "CscFeature_Setting_ConfigDefAppShortcutForLockScreen")
                ?.toString()
        }
    } catch (e: Exception) {
        null
    }

    val final = when {
        cscDefault.isNullOrBlank() || cscDefault.split(";").size < 4 -> {
            var left = "com.samsung.android.dialer/com.samsung.android.dialer.DialtactsActivity"
            val right = "com.sec.android.app.camera/com.sec.android.app.camera.Camera"

            if (isTablet) {
                left = when {
                    hasSPen && isNotesInstalled -> {
                        "com.samsung.android.app.notes/com.samsung.android.app.notes.memolist.MemoListActivity"
                    }
                    isSBrowserInstalled -> {
                        "com.sec.android.app.sbrowser/com.sec.android.app.sbrowser.SBrowserMainActivity"
                    }
                    else -> {
                        "com.android.chrome/com.google.android.apps.chrome.Main"
                    }
                }
            }

            LockscreenShortcuts.ShortcutInfo.ComponentValues(left, right).toSettingsString()
        }

        isTablet && cscDefault.split(";")[3] == "com.samsung.android.app.notes/com.samsung.android.app.notes.memolist.MemoListActivity" -> {
            val values = LockscreenShortcuts.ShortcutInfo.ComponentValues.fromString(cscDefault)

            val left = values.left
            val right = values.right

            values.left = right
            values.right = left

            values.toSettingsString()
        }

        else -> cscDefault
    }

    return final
}
