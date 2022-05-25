package com.zacharee1.systemuituner.util

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.ComponentInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.*
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES
import android.provider.Settings
import android.service.quicksettings.Tile
import android.text.TextUtils
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.SortedList
import com.topjohnwu.superuser.Shell
import com.zacharee1.systemuituner.R
import java.util.*
import java.util.regex.Pattern

val mainHandler = Handler(Looper.getMainLooper())

val Context.hasSdCard: Boolean
    get() = ContextCompat.getExternalFilesDirs(this, null).size >= 2

val ComponentInfo.component: ComponentName
    get() = ComponentName(packageName, name)

val TextView.hasEllipsis: Boolean
    get() {
        val truncateAt = ellipsize
        if (truncateAt == null || TextUtils.TruncateAt.MARQUEE == truncateAt) {
            return false
        }

        if (layout == null) {
            return false
        }

        for (line in 0 until layout.lineCount) {
            if (layout.getEllipsisCount(line) > 0) {
                return true
            }
        }

        return false
    }

fun Context.apiToName(api: Int): String {
    return resources.getString(
        when (api) {
            VERSION_CODES.M -> R.string.android_marshmallow
            VERSION_CODES.N -> R.string.android_nougat
            VERSION_CODES.N_MR1 -> R.string.android_nougat_7_1
            VERSION_CODES.O -> R.string.android_oreo
            VERSION_CODES.O_MR1 -> R.string.android_oreo_8_1
            VERSION_CODES.P -> R.string.android_pie
            VERSION_CODES.Q -> R.string.android_10
            VERSION_CODES.R -> R.string.android_11
            VERSION_CODES.S -> R.string.android_12
            32 -> R.string.android_12l
            else -> throw IllegalArgumentException("Invalid API level: $api")
        }
    )
}

@SuppressLint("InlinedApi")
fun Context.getNotificationSettingsForChannel(channel: String?): Intent {
    val intent = Intent()
    when {
        SDK_INT >= VERSION_CODES.P -> {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            if (channel != null) {
                intent.action = Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS
                intent.putExtra(Settings.EXTRA_CHANNEL_ID, channel)
            } else {
                intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            }
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        }
        SDK_INT >= VERSION_CODES.O -> {
            if (channel != null) {
                intent.action = Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS
                intent.putExtra(Settings.EXTRA_CHANNEL_ID, channel)
            } else {
                intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            }
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        }
        SDK_INT >= VERSION_CODES.N_MR1 -> {
            intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        }
    }

    return intent
}

fun <T> SortedList<T>.toList(): ArrayList<T> {
    val ret = ArrayList<T>()

    for (i in 0 until size()) {
        ret.add(this[i])
    }

    return ret
}

@SuppressLint("ResourceType")
fun ApplicationInfo.getColorPrimary(context: Context): Int {
    val res = try {
        context.packageManager.getResourcesForApplication(this)
    } catch (e: PackageManager.NameNotFoundException) {
        return 0
    } ?: return 0

    val theme = res.newTheme()
    val arr = intArrayOf(
        res.getIdentifier("colorPrimary", "attr", packageName),
        android.R.attr.colorPrimary,
        res.getIdentifier("colorPrimaryDark", "attr", packageName),
        android.R.attr.colorPrimaryDark,
        res.getIdentifier("colorAccent", "attr", packageName),
        android.R.attr.colorAccent
    )

    var color = 0

    try {
        theme.applyStyle(
            context.packageManager.run {
                getActivityInfo(
                    getLaunchIntentForPackage(packageName)
                        .component,
                    0
                ).theme
            },
            true
        )

        val attrs = theme.obtainStyledAttributes(arr)

        color = attrs.getColor(
            0,
            attrs.getColor(
                1,
                attrs.getColor(
                    2,
                    attrs.getColor(
                        3,
                        attrs.getColor(
                            4,
                            attrs.getColor(5, 0)
                        )
                    )
                )
            )
        )

        attrs.recycle()
    } catch (_: Exception) {
    }

    if (color == 0) {
        try {
            theme.applyStyle(
                this.theme,
                true
            )

            val attrs = theme.obtainStyledAttributes(arr)

            color = attrs.getColor(
                0,
                attrs.getColor(
                    1,
                    attrs.getColor(
                        2,
                        attrs.getColor(
                            3,
                            attrs.getColor(
                                4,
                                attrs.getColor(5, 0)
                            )
                        )
                    )
                )
            )

            attrs.recycle()
        } catch (_: Exception) {
        }
    }

    return color
}

fun parseAutoIconBlacklistSlots(alternate: Boolean = false): ArrayList<String> {
    val slots = ArrayList<String>()

    val lines = ArrayList<String>()
    val error = ArrayList<String>()

    val job = if (!alternate) {
        Shell.sh(
            "dumpsys activity service com.android.systemui/.SystemUIService Dependency | grep -E '^.*([0-9])+:.*\\(.*\\).*\$'\n"
        )
    } else {
        Shell.sh(
            "dumpsys activity service com.android.systemui/.SystemUIService | grep -E '^.*([0-9])+:\\(.*\\).*\$'\n"
        )
    }

    job.to(lines, error)
    job.exec()

    val parenPattern = Pattern.compile("([0-9])+:\\((.+?)\\)")

    lines.forEach {
        val matcher = parenPattern.matcher(it)
        matcher.find()
        try {
            slots.add(
                matcher.group()
                    .replace("(", "")
                    .replace(")", "")
                    .replace(Regex("([0-9])+:"), "")
            )
        } catch (_: IllegalStateException) {
        }
    }

    return if (slots.isEmpty() && !alternate) parseAutoIconBlacklistSlots(true) else slots
}

fun Context.launchUrl(url: String) {
    try {
        val browserIntent =
            Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    } catch (_: Exception) {
    }
}

fun Context.launchEmail(to: String, subject: String) {
    try {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.setDataAndType(Uri.parse(createEmailString(to, subject)), "text/plain")

        startActivity(intent)
    } catch (_: Exception) {
    }
}

fun createEmailString(to: String, subject: String): String {
    return "mailto:${Uri.encode(to)}?subject=${Uri.encode(subject)}"
}

fun Resources.getStringByName(name: String, pkg: String): String {
    val id = getIdentifier(name, "string", pkg)
    return getString(id)
}

fun String.toIntOrNullOnError(): Int? {
    return try {
        toInt()
    } catch (e: NumberFormatException) {
        null
    }
}

inline fun <T : IInterface> T.callSafely(block: (T) -> Unit) {
    try {
        block(this)
    } catch (_: Exception) {
    }
}

fun String.toFloatOrDefault(default: Float): Float {
    return try {
        this.toFloat()
    } catch (e: NumberFormatException) {
        default
    }
}

val String.capitalized: String
    get() = replaceFirstChar {
        if (it.isLowerCase()) {
            it.titlecase(Locale.getDefault())
        } else {
            it.toString()
        }
    }

@Suppress("DEPRECATION")
fun Drawable.setColorFilterCompat(color: Int, mode: PorterDuff.Mode) {
    if (SDK_INT >= VERSION_CODES.Q) {
        colorFilter = BlendModeColorFilter(color, BlendMode.valueOf(mode.toString()))
    } else {
        setColorFilter(color, mode)
    }
}

fun Context.isComponentEnabled(componentName: ComponentName): Boolean {
    return when (packageManager.getComponentEnabledSetting(componentName)) {
        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
        PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED,
        PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER -> {
            false
        }
        PackageManager.COMPONENT_ENABLED_STATE_DEFAULT -> {
            val info = packageManager.getPackageInfo(
                packageName,
                PackageManager.GET_ACTIVITIES or
                        PackageManager.GET_RECEIVERS or
                        PackageManager.GET_SERVICES or
                        PackageManager.GET_PROVIDERS or
                        PackageManager.GET_DISABLED_COMPONENTS
            )

            val components = arrayListOf<ComponentInfo>()
            info.activities?.let { components.addAll(it) }
            info.services?.let { components.addAll(it) }
            info.receivers?.let { components.addAll(it) }
            info.providers?.let { components.addAll(it) }

            return components.filter { component ->
                ComponentName(
                    component.packageName,
                    component.name
                ) == componentName
            }
                .apply {
                    if (this.isEmpty()) throw IllegalArgumentException("Component $componentName not found")
                }[0].isEnabled
        }
        else -> true
    }
}

@RequiresApi(VERSION_CODES.N)
fun Tile.safeUpdateTile() {
    try {
        updateTile()
    } catch (_: Exception) {}
}
