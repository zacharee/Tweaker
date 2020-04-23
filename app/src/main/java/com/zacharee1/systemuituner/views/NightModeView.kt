package com.zacharee1.systemuituner.views

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.AdapterView
import androidx.core.view.isVisible
import com.zacharee1.systemuituner.data.NightModeInfo
import com.zacharee1.systemuituner.interfaces.IOptionDialogCallback
import com.zacharee1.systemuituner.util.SettingsType
import com.zacharee1.systemuituner.util.getSetting
import kotlinx.android.synthetic.main.night_mode.view.*
import tk.zwander.seekbarpreference.SeekBarView

class NightModeView(context: Context, attrs: AttributeSet) : RoundedFrameCardView(context, attrs), IOptionDialogCallback {
    companion object {
        const val TWILIGHT_MODE = "twilight_mode"
        const val NIGHT_DISPLAY_ACTIVATED = "night_display_activated"
        const val NIGHT_DISPLAY_AUTO_MODE = "night_display_auto_mode"
        const val NIGHT_DISPLAY_COLOR_TEMPERATURE = "night_display_color_temperature"
        const val NIGHT_DISPLAY_CUSTOM_START_TIME = "night_display_custom_start_time"
        const val NIGHT_DISPLAY_CUSTOM_END_TIME = "night_display_custom_end_time"

        const val TWILIGHT_OFF = 0
        const val TWILIGHT_ON = 1
        const val TWILIGHT_AUTO = 2
    }

    override var callback: ((data: Any?) -> Unit)? = null
    private val nightModeInfo = NightModeInfo()

    override fun onFinishInflate() {
        super.onFinishInflate()

        val atLeastNMR1 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1

        night_display_wrapper.isVisible = atLeastNMR1
        twilight_wrapper.isVisible = !atLeastNMR1
        reset_night_mode.setOnClickListener {
            nightModeInfo.nullAll()
            callback?.invoke(nightModeInfo)
            updateStates()
        }

        updateStates()
    }

    private fun updateStates() {
        val atLeastNMR1 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1

        nightModeInfo.nightModeActivated = context.getSetting(SettingsType.SECURE, NIGHT_DISPLAY_ACTIVATED)?.toIntOrNull()
        nightModeInfo.nightModeAuto = context.getSetting(SettingsType.SECURE, NIGHT_DISPLAY_AUTO_MODE)?.toIntOrNull()
        nightModeInfo.nightModeTemp = context.getSetting(SettingsType.SECURE, NIGHT_DISPLAY_COLOR_TEMPERATURE, "5000")?.toIntOrNull()
        nightModeInfo.twilightMode = context.getSetting(SettingsType.SECURE, TWILIGHT_MODE, TWILIGHT_OFF.toString())?.toIntOrNull()

        if (atLeastNMR1) {
            night_display_enabled.isChecked = nightModeInfo.nightModeActivated == 1
            night_display_enabled.setOnCheckedChangeListener { _, isChecked ->
                nightModeInfo.nightModeActivated = if (isChecked) 1 else 0
                callback?.invoke(nightModeInfo)
            }

            night_display_auto.isChecked = nightModeInfo.nightModeAuto == 1
            night_display_auto.setOnCheckedChangeListener { _, isChecked ->
                nightModeInfo.nightModeAuto = if (isChecked) 1 else 0
                callback?.invoke(nightModeInfo)
            }

            night_display_temp.minValue = resources.run {
                getInteger(getIdentifier("config_nightDisplayColorTemperatureMin", "integer", "android"))
            }
            night_display_temp.maxValue = resources.run {
                getInteger(getIdentifier("config_nightDisplayColorTemperatureMax", "integer", "android"))
            }
            night_display_temp.defaultValue = resources.run {
                getInteger(getIdentifier("config_nightDisplayColorTemperatureDefault", "integer", "android"))
            }
            night_display_temp.scaledProgress = nightModeInfo.nightModeTemp?.toFloat() ?: 5000f
            night_display_temp.listener = object : SeekBarView.SeekBarListener {
                override fun onProgressAdded() {}
                override fun onProgressReset() {}
                override fun onProgressSubtracted() {}
                override fun onProgressChanged(newValue: Int, newScaledValue: Float) {
                    nightModeInfo.nightModeTemp = newScaledValue.toInt()
                    callback?.invoke(nightModeInfo)
                }
            }
        } else {
            twilight_mode.setSelection(nightModeInfo.twilightMode ?: TWILIGHT_OFF)
            twilight_mode.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    nightModeInfo.twilightMode = position
                    callback?.invoke(nightModeInfo)
                }
            }
        }
    }
}