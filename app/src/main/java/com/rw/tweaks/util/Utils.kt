package com.rw.tweaks.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.N_MR1
import android.os.Build.VERSION_CODES.O
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.TypedValue
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.rw.tweaks.R
import java.util.*
import kotlin.math.roundToInt

enum class SettingsType(val value: Int) {
    UNDEFINED(-1),
    GLOBAL(0),
    SECURE(1),
    SYSTEM(2);

    companion object {
        const val UNDEFINED_LITERAL = "undefined"
        const val GLOBAL_LITERAL = "global"
        const val SECURE_LITERAL = "secure"
        const val SYSTEM_LITERAL = "system"

        fun fromString(input: String): SettingsType {
            return when (input.toLowerCase(Locale.getDefault())) {
                GLOBAL_LITERAL -> GLOBAL
                SECURE_LITERAL -> SECURE
                SYSTEM_LITERAL -> SYSTEM
                else -> UNDEFINED
            }
        }
    }

    override fun toString(): String {
        return when (this) {
            UNDEFINED -> UNDEFINED_LITERAL
            GLOBAL -> GLOBAL_LITERAL
            SECURE -> SECURE_LITERAL
            SYSTEM -> SYSTEM_LITERAL
        }
    }
}

val mainHandler = Handler(Looper.getMainLooper())

val api: Int = SDK_INT

val Context.prefManager: PrefManager
    get() = PrefManager.getInstance(this)

val Context.hasSdCard: Boolean
    get() = ContextCompat.getExternalFilesDirs(this, null).size >= 2

val Context.hasWss: Boolean
    get() = checkCallingOrSelfPermission(android.Manifest.permission.WRITE_SECURE_SETTINGS) == PackageManager.PERMISSION_GRANTED

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

fun Context.apiToName(api: Int): String {
    return resources.getString(when (api) {
        Build.VERSION_CODES.M -> R.string.android_marshmallow
        Build.VERSION_CODES.N -> R.string.android_nougat
        Build.VERSION_CODES.N_MR1 -> R.string.android_nougat_7_1
        Build.VERSION_CODES.O -> R.string.android_oreo
        Build.VERSION_CODES.O_MR1 -> R.string.android_oreo_8_1
        Build.VERSION_CODES.P -> R.string.android_pie
        29 -> R.string.android_10
        else -> throw IllegalArgumentException("Invalid API level: $api")
    })
}

fun Context.dpAsPx(dpVal: Number) =
    TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dpVal.toFloat(),
        resources.displayMetrics
    ).roundToInt()

fun Context.getNotificationSettingsForChannel(channel: String?): Intent {
    val intent = Intent()
    when {
        SDK_INT >= Build.VERSION_CODES.P -> {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            if (channel != null) {
                intent.action = Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS
                intent.putExtra(Settings.EXTRA_CHANNEL_ID, channel)
            } else {
                intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            }
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        }
        SDK_INT >= O -> {
            if (channel != null) {
                intent.action = Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS
                intent.putExtra(Settings.EXTRA_CHANNEL_ID, channel)
            } else {
                intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            }
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        }
        SDK_INT >= N_MR1 -> {
            intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        }
    }

    return intent
}
