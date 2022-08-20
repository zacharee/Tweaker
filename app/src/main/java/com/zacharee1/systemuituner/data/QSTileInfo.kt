package com.zacharee1.systemuituner.data

import android.content.ComponentName
import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.util.capitalized
import java.util.regex.Pattern

class QSTileInfo(
    val key: String
) {
    enum class Type {
        INTENT,
        CUSTOM,
        STANDARD
    }

    private var _label: String? = null
    private var _icon: Drawable? = null

    val type: Type = when {
        key.contains("intent(") -> Type.INTENT
        key.contains("custom(") -> Type.CUSTOM
        else -> Type.STANDARD
    }

    fun getLabel(context: Context): String {
        return _label ?: when (type) {
            Type.INTENT -> getIntentLabel()
            Type.CUSTOM -> context.getCustomLabel()
            Type.STANDARD -> key.capitalized
        }.also { _label = it }
    }

    fun getIcon(context: Context): Drawable? {
        return _icon ?: when (type) {
            Type.INTENT -> context.getDefaultDrawable()
            Type.CUSTOM -> context.getCustomDrawable()
            Type.STANDARD -> context.chooseStandardDrawable()
        }.also { _icon = it }
    }

    private fun getIntentLabel(): String {
        val p = Pattern.compile("\\((.*?)\\)")
        val m = p.matcher(key)

        var title = ""

        while (!m.hitEnd()) {
            if (m.find()) title = m.group()
        }

        return title.replace("(", "").replace(")", "")
    }

    private fun Context.getCustomLabel(): String {
        val component = getNameAndComponentForCustom()

        return try {
            if (component != null) {
                packageManager.getServiceInfo(component, 0).loadLabel(packageManager)
                    .toString()
            } else {
                packageManager.getApplicationInfo(packageName, 0).loadLabel(packageManager)
                    .toString()
            }
        } catch (e: Exception) {
            try {
                component?.className?.split(".")?.run { this[size - 1] } ?: packageName
            } catch (e: Exception) {
                packageName
            }
        }
    }

    fun getNameAndComponentForCustom(): ComponentName? {
        val p = Pattern.compile("\\((.*?)\\)")
        val m = p.matcher(key)

        var name = ""

        while (!m.hitEnd()) {
            if (m.find()) name = m.group()
        }

        name = name.replace("(", "").replace(")", "")

        return ComponentName.unflattenFromString(name)
    }

    private fun Context.getCustomDrawable(): Drawable? {
        val component = getNameAndComponentForCustom()

        return try {
            packageManager.getServiceInfo(component, 0).loadIcon(packageManager)
        } catch (e: Exception) {
            try {
                packageManager.getApplicationInfo(packageName, 0).loadIcon(packageManager)
            } catch (e: Exception) {
                getDefaultDrawable()
            }
        }?.mutate()
    }

    private fun Context.chooseStandardDrawable(): Drawable? {
        return ResourcesCompat.getDrawable(
            resources,
            when (key.lowercase()) {
                "wifi" -> R.drawable.ic_baseline_signal_wifi_4_bar_24
                "bluetooth", "bt" -> R.drawable.ic_baseline_bluetooth_24
                "color_inversion" -> R.drawable.ic_baseline_invert_colors_24
                "cell" -> R.drawable.ic_baseline_signal_cellular_4_bar_24
                "do_not_disturb", "dnd" -> R.drawable.do_not_disturb
                "airplane", "airplanemode" -> R.drawable.ic_baseline_airplanemode_active_24
                "cast" -> R.drawable.ic_baseline_cast_24
                "location" -> R.drawable.ic_baseline_location_on_24
                "rotation", "rotationlock" -> R.drawable.ic_baseline_screen_rotation_24
                "flashlight" -> R.drawable.ic_baseline_highlight_24
                "hotspot" -> R.drawable.ic_baseline_wifi_tethering_24
                "battery" -> R.drawable.battery_full
                "sound", "soundmode" -> R.drawable.ic_baseline_volume_up_24
                "sync" -> R.drawable.ic_baseline_sync_24
                "nfc" -> R.drawable.ic_baseline_nfc_24
                "data", "mobiledata" -> R.drawable.ic_baseline_data_usage_24
                "night", "moonlight", "bluelightfilter" -> R.drawable.ic_baseline_nights_stay_24
                "smarthome", "home" -> R.drawable.ic_baseline_home_24
                "privacy" -> R.drawable.ic_baseline_security_24
                "camera" -> R.drawable.ic_baseline_camera_24
                "lowpower", "battery_saver", "batterymode", "powerplanning" -> R.drawable.ic_baseline_battery_plus_24
                "exitkft" -> R.drawable.ic_baseline_child_care_24
                "recordscreentile", "screenrecorder" -> R.drawable.ic_baseline_videocam_24
                "screencapture" -> R.drawable.ic_baseline_camera_24
                "internet" -> R.drawable.ic_network
                "controls" -> R.drawable.ic_baseline_home_24
                "mictoggle" -> R.drawable.microphone
                "cameratoggle" -> R.drawable.ic_baseline_camera_24
                "screenrecord" -> R.drawable.record
                "wallet" -> R.drawable.credit_card
                else -> R.drawable.ic_baseline_android_24
            },
            theme
        )
    }

    private fun Context.getDefaultDrawable(): Drawable? {
        return ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_android_24, theme)
    }
}