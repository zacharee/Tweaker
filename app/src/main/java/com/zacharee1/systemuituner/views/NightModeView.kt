package com.zacharee1.systemuituner.views

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.zacharee1.systemuituner.data.NightModeInfo
import com.zacharee1.systemuituner.databinding.NightModeBinding
import com.zacharee1.systemuituner.interfaces.IOptionDialogCallback
import com.zacharee1.systemuituner.data.SettingsType
import com.zacharee1.systemuituner.util.getSetting
import tk.zwander.seekbarpreference.SeekBarView

class NightModeView(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs), IOptionDialogCallback {
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

    private val binding by lazy { NightModeBinding.bind(this) }

    override fun onFinishInflate() {
        super.onFinishInflate()

        val atLeastNMR1 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1

        binding.nightDisplayWrapper.isVisible = atLeastNMR1
        binding.twilightWrapper.isVisible = !atLeastNMR1
        binding.resetNightMode.setOnClickListener {
            nightModeInfo.nullAll()
            callback?.invoke(nightModeInfo)
            updateStates()
        }

        updateStates()
    }

    private fun updateStates() {
        val atLeastNMR1 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1

        val minValue = resources.run {
            try {
                getInteger(getIdentifier("config_nightDisplayColorTemperatureMin", "integer", "android"))
            } catch (e: Resources.NotFoundException) {
                0
            }
        }
        val maxValue = resources.run {
            try {
                getInteger(getIdentifier("config_nightDisplayColorTemperatureMax", "integer", "android"))
            } catch (e: Resources.NotFoundException) {
                10000
            }
        }
        val defaultValue = resources.run {
            try {
                getInteger(getIdentifier("config_nightDisplayColorTemperatureDefault", "integer", "android"))
            } catch (e: Resources.NotFoundException) {
                5000.coerceAtMost(maxValue).coerceAtLeast(minValue)
            }
        }

        nightModeInfo.nightModeActivated = context.getSetting(SettingsType.SECURE, NIGHT_DISPLAY_ACTIVATED)?.toIntOrNull()
        nightModeInfo.nightModeAuto = context.getSetting(SettingsType.SECURE, NIGHT_DISPLAY_AUTO_MODE)?.toIntOrNull()
        nightModeInfo.nightModeTemp = context.getSetting(SettingsType.SECURE, NIGHT_DISPLAY_COLOR_TEMPERATURE, defaultValue)?.toIntOrNull()
        nightModeInfo.twilightMode = context.getSetting(SettingsType.SECURE, TWILIGHT_MODE, TWILIGHT_OFF.toString())?.toIntOrNull()

        if (atLeastNMR1) {
            binding.nightDisplayEnabled.isChecked = nightModeInfo.nightModeActivated == 1
            binding.nightDisplayEnabled.setOnCheckedChangeListener { _, isChecked ->
                nightModeInfo.nightModeActivated = if (isChecked) 1 else 0
                callback?.invoke(nightModeInfo)
            }

            binding.nightDisplayAuto.isChecked = nightModeInfo.nightModeAuto == 1
            binding.nightDisplayAuto.setOnCheckedChangeListener { _, isChecked ->
                nightModeInfo.nightModeAuto = if (isChecked) 1 else 0
                callback?.invoke(nightModeInfo)
            }

//            binding.nightDisplayTemp.minValue = resources.run {
//                try {
//                    getInteger(getIdentifier("config_nightDisplayColorTemperatureMin", "integer", "android"))
//                } catch (e: Resources.NotFoundException) {
//                    0
//                }
//            }
//            binding.nightDisplayTemp.maxValue = resources.run {
//                try {
//                    getInteger(getIdentifier("config_nightDisplayColorTemperatureMax", "integer", "android"))
//                } catch (e: Resources.NotFoundException) {
//                    10000
//                }
//            }
//            binding.nightDisplayTemp.defaultValue = resources.run {
//                try {
//                    getInteger(getIdentifier("config_nightDisplayColorTemperatureDefault", "integer", "android"))
//                } catch (e: Resources.NotFoundException) {
//                    5000
//                }
//            }
//            binding.nightDisplayTemp.scaledProgress = nightModeInfo.nightModeTemp?.toFloat() ?: 5000f
//            binding.nightDisplayTemp.listener = object : SeekBarView.SeekBarListener {
//                override fun onProgressAdded() {}
//                override fun onProgressReset() {}
//                override fun onProgressSubtracted() {}
//                override fun onProgressChanged(newValue: Int, newScaledValue: Float) {
//                    nightModeInfo.nightModeTemp = newScaledValue.toInt()
//                    callback?.invoke(nightModeInfo)
//                }
//            }

            binding.nightDisplayTemp.onBind(
                minValue = minValue,
                maxValue = maxValue,
                progress = nightModeInfo.nightModeTemp ?: 5000,
                defaultValue = defaultValue,
                scale = 1f,
                units = null,
                key = "",
                listener = object : SeekBarView.SeekBarListener {
                    override fun onProgressAdded() {}
                    override fun onProgressReset() {}
                    override fun onProgressSubtracted() {}
                    override fun onProgressChanged(newValue: Int, newScaledValue: Float) {
                        nightModeInfo.nightModeTemp = newScaledValue.toInt()
                        callback?.invoke(nightModeInfo)
                    }
                },
                prefs = null
            )
        } else {
            binding.twilightMode.setSelection(nightModeInfo.twilightMode ?: TWILIGHT_OFF)
            binding.twilightMode.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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