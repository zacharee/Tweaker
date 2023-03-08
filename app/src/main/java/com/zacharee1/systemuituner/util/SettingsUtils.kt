package com.zacharee1.systemuituner.util

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.CountDownTimer
import android.os.SystemProperties
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.topjohnwu.superuser.Shell
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.activities.RecommendSystemAddOn
import com.zacharee1.systemuituner.data.SettingsType
import com.zacharee1.systemuituner.data.WriteSystemAddOnValues
import com.zacharee1.systemuituner.dialogs.RoundedBottomSheetDialog
import com.zacharee1.systemuituner.views.LockscreenShortcuts
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import rikka.shizuku.Shizuku
import java.util.HashSet
import kotlin.concurrent.timer

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
                    suspend {
                        originalValues.forEach { (info, setting) ->
                            writeSetting(info.first.type, info.first.key, setting, false, saveOption)
                        }
                    }
                    dialog.dismiss()
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
        if (Shizuku.pingBinder() && hasShizukuPermission) {
            @Suppress("DEPRECATION")
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

private fun Context.writeGlobal(key: String?, value: Any?): Boolean {
    if (key.isNullOrBlank()) return false
    return try {
        Settings.Global.putString(contentResolver, key, value?.toString())
        true
    } catch (e: SecurityException) {
        Log.e("SystemUI Tuner", "Failed to write to Global", e)
        false
    }
}

private fun Context.writeSecure(key: String?, value: Any?): Boolean {
    if (key.isNullOrBlank()) return false
    return try {
        Settings.Secure.putString(contentResolver, key, value?.toString())
        true
    } catch (e: SecurityException) {
        Log.e("SystemUI Tuner", "Failed to write to Secure", e)
        false
    }
}

private fun Context.writeSystem(key: String?, value: Any?): Boolean {
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
                try {
                    @Suppress("DEPRECATION")
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
                } catch (e: Throwable) {
                    Log.e("SystemUI Tuner", "Failed to write to System", e)
                    false
                }
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
    }.also {
        if (!it) {
            RecommendSystemAddOn.start(this)
        }
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
        packageManager.getPackageInfoCompat("tk.zwander.systemuituner.systemsettings")
        true
    } catch (e: Exception) {
        false
    }
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
