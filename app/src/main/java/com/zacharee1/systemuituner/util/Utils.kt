package com.zacharee1.systemuituner.util

import android.animation.LayoutTransition
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.ComponentInfo
import android.content.pm.IPackageManager
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
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.preference.Preference
import androidx.preference.PreferenceGroup
import androidx.preference.PreferenceGroupAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import com.topjohnwu.superuser.Shell
import com.zacharee1.systemuituner.R
import rikka.shizuku.*
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList
import kotlin.collections.HashSet
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
            return when (input.lowercase(Locale.getDefault())) {
                GLOBAL_LITERAL -> GLOBAL
                SECURE_LITERAL -> SECURE
                SYSTEM_LITERAL -> SYSTEM
                else -> UNDEFINED
            }
        }

        fun fromValue(value: Int): SettingsType {
            return when(value) {
                0 -> GLOBAL
                1 -> SECURE
                2 -> SYSTEM
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

val Context.hasDump: Boolean
    get() = checkCallingOrSelfPermission(android.Manifest.permission.DUMP) == PackageManager.PERMISSION_GRANTED

val Context.hasPackageUsageStats: Boolean
    get() = checkCallingOrSelfPermission(android.Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED

val Context.isTouchWiz: Boolean
    get() = packageManager.hasSystemFeature("com.samsung.feature.samsung_experience_mobile")

val ComponentInfo.component: ComponentName
    get() = ComponentName(packageName, name)

val isHTC: Boolean
    get() = !SystemProperties.get("ro.build.sense.version").isNullOrBlank()

val isLG: Boolean
    get() = !SystemProperties.get("ro.lge.lguiversion").isNullOrBlank()

val isHuawei: Boolean
    get() = !SystemProperties.get("ro.build.hw_emui_api_level").isNullOrBlank()

val isXiaomi: Boolean
    get() = !SystemProperties.get("ro.miui.ui.version.code").isNullOrBlank()

val Preference.defaultValue: Any?
    get() {
        return Preference::class.java
            .getDeclaredField("mDefaultValue")
            .apply { isAccessible = true }
            .get(this)
    }

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
    } catch (e: SecurityException) {
    }

    try {
        Settings.Secure.resetToDefaults(contentResolver, null)
    } catch (e: SecurityException) {
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
                Shell.su("content insert --uri content://settings/system --bind name:s:$key --bind value:s:$value --bind package:s:$packageName").exec()
                true
            }
            isAddOnInstalled() -> {
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

fun Fragment.updateTitle(title: Int) {
    activity?.setTitle(title)
}

fun Fragment.updateTitle(title: CharSequence?) {
    activity?.title = title
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
            else -> throw IllegalArgumentException("Invalid API level: $api")
        }
    )
}

fun Context.dpAsPx(dpVal: Number) =
    TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dpVal.toFloat(),
        resources.displayMetrics
    ).roundToInt()

fun Context.asDp(value: Number) =
    value.toFloat() / resources.displayMetrics.density

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

inline fun PreferenceGroup.forEach(block: (index: Int, child: Preference) -> Unit) {
    for (i in 0 until preferenceCount) {
        block(i, getPreference(i))
    }
}

fun PreferenceGroup.hasPreference(key: String): Boolean {
    forEach { _, child ->
        if (key == child.key) return@hasPreference true
    }

    return false
}

fun PreferenceGroup.indexOf(preference: Preference): Int {
    forEach { index, child ->
        if (child == preference) return index
    }

    return -1
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
    } catch (e: Exception) {
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
        } catch (e: Exception) {
        }
    }

    return color
}

fun SearchView.addAnimation() {
    val bar = findViewById<LinearLayout>(R.id.search_bar)
    bar.layoutTransition = LayoutTransition().apply {
        this.enableTransitionType(LayoutTransition.CHANGING)
    }
}

fun Toolbar.addAnimation() {
    layoutTransition = LayoutTransition().apply {
        this.enableTransitionType(LayoutTransition.CHANGING)
    }
}

fun PreferenceGroupAdapter.updatePreferences() {
    PreferenceGroupAdapter::class.java
        .getDeclaredMethod("updatePreferences")
        .apply { isAccessible = true }
        .invoke(this)
}

fun Context.buildNonResettablePreferences(): Set<String> {
    val names = HashSet<String>()
    try {
        val cursor = contentResolver.query(Uri.parse("content://settings/system"), arrayOf("name", "package"), null, null, null)
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val pkg = cursor.getString(1)
                if (pkg == packageName || pkg == "tk.zwander.systemuituner.systemsettings") {
                    names.add(cursor.getString(0))
                }
            }
            cursor.close()
        }
    } catch (e: IllegalArgumentException) {}
    names.addAll(prefManager.savedOptions.filter { it.type == SettingsType.SYSTEM }.map { it.key })
    return names
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
            slots.add(matcher.group()
                .replace("(", "")
                .replace(")", "")
                .replace(Regex("([0-9])+:"), "")
            )
        } catch (e: IllegalStateException) {}
    }

    return if (slots.isEmpty() && !alternate) parseAutoIconBlacklistSlots(true) else slots
}

fun View.scaleAnimatedVisible(visible: Boolean, listener: Animation.AnimationListener? = null) {
    val anim = AnimationUtils.loadAnimation(context, if (visible) R.anim.scale_in else R.anim.scale_out)
    anim.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationRepeat(animation: Animation?) {
            listener?.onAnimationRepeat(animation)
        }
        override fun onAnimationStart(animation: Animation?) {
            listener?.onAnimationStart(animation)
        }
        override fun onAnimationEnd(animation: Animation?) {
            if (!visible) {
                isVisible = false
                alpha = 0f
            } else {
                alpha = 1f
            }
            listener?.onAnimationEnd(animation)
        }
    })
    if (visible) {
        isVisible = true
        alpha = 0f
    } else {
        alpha = 1f
    }
    startAnimation(anim)
}

var View.scaleAnimatedVisible: Boolean
    get() = isVisible
    set(value) {
        scaleAnimatedVisible(value)
    }

object WriteSystemAddOnValues {
    const val ACTION_WRITE_SYSTEM = "com.zacharee1.systemuituner.WRITE_SYSTEM"
    const val ACTION_WRITE_SYSTEM_RESULT = "com.zacharee1.systemuituner.WRITE_SYSTEM_RESULT"

    const val EXTRA_KEY = "WRITE_SYSTEM_KEY"
    const val EXTRA_VALUE = "WRITE_SYSTEM_VALUE"
    const val EXTRA_EXCEPTION = "WRITE_SYSTEM_EXCEPTION"

    const val PERMISSION = "com.zacharee1.systemuituner.permission.WRITE_SETTINGS"
    const val WRITE_SYSTEM_REQUEST_CODE = 123456
}

fun Context.writeSystemSettingsWithAddOnNoResult(key: String?, value: Any?) {
    val intent = Intent(WriteSystemAddOnValues.ACTION_WRITE_SYSTEM)
    intent.putExtra(WriteSystemAddOnValues.EXTRA_KEY, key)
    intent.putExtra(WriteSystemAddOnValues.EXTRA_VALUE, value?.toString())
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    intent.setClassName("tk.zwander.systemuituner.systemsettings", "tk.zwander.systemuituner.systemsettings.WriteSystemActivity")

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
    intent.setClassName("tk.zwander.systemuituner.systemsettings", "tk.zwander.systemuituner.systemsettings.WriteSystemActivity")

    try {
        startActivityForResult(intent, WriteSystemAddOnValues.WRITE_SYSTEM_REQUEST_CODE)
    } catch (e: ActivityNotFoundException) {
        Log.e("SystemUITuner", "Add-on error", e)
    }
}

fun Fragment.writeSystemSettingsWithAddOnResult(key: String?, value: Any?) {
    requireActivity().writeSystemSettingsWithAddOnResult(key, value)
}

fun Context.isAddOnInstalled(): Boolean {
    return try {
        packageManager.getPackageInfo("tk.zwander.systemuituner.systemsettings", 0)
        true
    } catch (e: Exception) {
        false
    }
}

fun Context.launchUrl(url: String) {
    try {
        val browserIntent =
            Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    } catch (e: Exception) {}
}

fun Context.launchEmail(to: String, subject: String) {
    try {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.type = "text/plain"
        intent.data = Uri.parse("mailto:${Uri.encode(to)}?subject=${Uri.encode(subject)}")

        startActivity(intent)
    } catch (e: Exception) {}
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
    } catch (e: Exception) {}
}

fun String.toFloatOrDefault(default: Float): Float {
    return try {
        this.toFloat()
    } catch (e: NumberFormatException) {
        default
    }
}

fun Context.grantPermissionThroughShizuku(permission: String): Boolean {
    return try {
        val ipm = IPackageManager.Stub.asInterface(ShizukuBinderWrapper(SystemServiceHelper.getSystemService("package")))

        ipm.grantRuntimePermission(packageName, permission, UserHandle.USER_SYSTEM)

        true
    } catch (e: IllegalStateException) {
        false
    }
}

fun Context.requestShizukuPermission(code: Int) {
    if (Shizuku.isPreV11() || Shizuku.getVersion() < 11) {
        if (this is Activity) {
            requestPermissions(arrayOf(ShizukuProvider.PERMISSION), code)
        } else if (this is Fragment) {
            requestPermissions(arrayOf(ShizukuProvider.PERMISSION), code)
        }
    } else {
        Shizuku.requestPermission(code)
    }
}

val Context.hasShizukuPermission: Boolean
    get() = if (Shizuku.isPreV11() || Shizuku.getVersion() < 11) {
        checkCallingOrSelfPermission(ShizukuProvider.PERMISSION) == PackageManager.PERMISSION_GRANTED
    } else {
        Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
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

            return components.filter { component -> ComponentName(component.packageName, component.name) == componentName }
                .apply {
                    if (this.isEmpty()) throw IllegalArgumentException("Component $componentName not found")
                }[0].isEnabled
        }
        else -> true
    }
}

fun Fragment.chooseLayoutManager(view: View?, grid: RecyclerView.LayoutManager, linear: RecyclerView.LayoutManager, extraFlags: Boolean = true): RecyclerView.LayoutManager {
    return if (extraFlags && (requireContext().asDp(view?.width ?: 0)) >= 800) {
        grid
    } else {
        linear
    }
}

fun Fragment.updateLayoutManager(view: View?, recycler: RecyclerView?, grid: RecyclerView.LayoutManager, linear: RecyclerView.LayoutManager, extraFlags: Boolean = true) {
    recycler?.layoutManager = chooseLayoutManager(view, grid, linear, extraFlags)
}
