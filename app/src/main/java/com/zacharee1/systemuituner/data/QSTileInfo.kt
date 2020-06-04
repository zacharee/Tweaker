package com.zacharee1.systemuituner.data

import android.content.ComponentName
import android.content.Context
import android.graphics.drawable.Drawable
import com.zacharee1.systemuituner.R
import java.util.*
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

    @ExperimentalStdlibApi
    fun getLabel(context: Context): String {
        return _label ?: when (type) {
            Type.INTENT -> getIntentLabel()
            Type.CUSTOM -> context.getCustomLabel()
            Type.STANDARD -> key.capitalize(Locale.US)
        }.also { _label = it }
    }

    fun getIcon(context: Context): Drawable {
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
        val (packageName, component) = getNameAndComponentForCustom()

        return try {
            packageManager.getServiceInfo(ComponentName(packageName, "$packageName$component"), 0).loadLabel(packageManager)
                .toString()
        } catch (e: Exception) {
            try {
                component.split(".").run { this[size - 1] }
            } catch (e: Exception) {
                packageName
            }
        }
    }

    private fun getNameAndComponentForCustom(): Pair<String, String> {
        val p = Pattern.compile("\\((.*?)\\)")
        val m = p.matcher(key)

        var name = ""

        while (!m.hitEnd()) {
            if (m.find()) name = m.group()
        }

        name = name.replace("(", "").replace(")", "")

        val packageName = name.split("/")[0]
        val component = name.split("/")[1]

        return packageName to component
    }

    private fun Context.getCustomDrawable(): Drawable {
        val (packageName, component) = getNameAndComponentForCustom()

        return try {
            packageManager.getServiceInfo(ComponentName(packageName, component), 0).loadIcon(packageManager)
        } catch (e: Exception) {
            getDefaultDrawable()
        }
    }

    private fun Context.chooseStandardDrawable(): Drawable {
        return resources.getDrawable(
            when (key.toLowerCase(Locale.US)) {
                "wifi" -> R.drawable.ic_baseline_signal_wifi_4_bar_24
                "bluetooth" -> R.drawable.ic_baseline_bluetooth_24
                "color_inversion" -> R.drawable.ic_baseline_invert_colors_24
                "cell" -> R.drawable.ic_baseline_signal_cellular_4_bar_24
                "do_not_disturb" -> R.drawable.do_not_disturb
                "airplane" -> R.drawable.ic_baseline_airplanemode_active_24
                "cast" -> R.drawable.ic_baseline_cast_24
                "location" -> R.drawable.ic_baseline_location_on_24
                "rotation" -> R.drawable.ic_baseline_screen_rotation_24
                "flashlight" -> R.drawable.ic_baseline_highlight_24
                "hotspot" -> R.drawable.ic_baseline_wifi_tethering_24
                "battery" -> R.drawable.battery_full
                "sound" -> R.drawable.ic_baseline_volume_up_24
                "sync" -> R.drawable.ic_baseline_sync_24
                "nfc" -> R.drawable.ic_baseline_nfc_24
                "data" -> R.drawable.ic_baseline_data_usage_24
                else -> R.drawable.ic_baseline_android_24
            },
            null
        )
    }

    private fun Context.getDefaultDrawable(): Drawable {
        return resources.getDrawable(R.drawable.ic_baseline_android_24, null)
    }
}