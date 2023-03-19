package com.zacharee1.systemuituner.data

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import com.zacharee1.systemuituner.util.getSetting
import com.zacharee1.systemuituner.views.NightModeView

val Context.minNightTemp: Int
    @SuppressLint("DiscouragedApi")
    get() = resources.run {
        try {
            getInteger(getIdentifier("config_nightDisplayColorTemperatureMin", "integer", "android"))
        } catch (e: Resources.NotFoundException) {
            0
        }
    }

val Context.maxNightTemp: Int
    @SuppressLint("DiscouragedApi")
    get() = resources.run {
        try {
            getInteger(getIdentifier("config_nightDisplayColorTemperatureMax", "integer", "android"))
        } catch (e: Resources.NotFoundException) {
            10000
        }
    }

val Context.defaultNightTemp: Int
    @SuppressLint("DiscouragedApi")
    get() = resources.run {
        try {
            getInteger(getIdentifier("config_nightDisplayColorTemperatureDefault", "integer", "android"))
        } catch (e: Resources.NotFoundException) {
            5000.coerceAtMost(maxNightTemp).coerceAtLeast(minNightTemp)
        }
    }

data class NightModeInfo(
    var twilightMode: Int? = 0,
    var nightModeActivated: Int? = 0,
    var nightModeAuto: Int? = 0,
    var nightModeTemp: Int? = 0
) {
    fun nullAll() {
        twilightMode = null
        nightModeActivated = null
        nightModeAuto = null
        nightModeTemp = null
    }

    fun populate(context: Context) {
        nightModeActivated = context.getSetting(SettingsType.SECURE,
            NightModeView.NIGHT_DISPLAY_ACTIVATED
        )?.toIntOrNull()
        nightModeAuto = context.getSetting(SettingsType.SECURE,
            NightModeView.NIGHT_DISPLAY_AUTO_MODE
        )?.toIntOrNull()
        nightModeTemp = context.getSetting(SettingsType.SECURE,
            NightModeView.NIGHT_DISPLAY_COLOR_TEMPERATURE, context.defaultNightTemp)?.toIntOrNull()
        twilightMode = context.getSetting(SettingsType.SECURE,
            NightModeView.TWILIGHT_MODE, NightModeView.TWILIGHT_OFF.toString())?.toIntOrNull()
    }
}