package com.zacharee1.systemuituner.views

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.AdapterView
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.zacharee1.systemuituner.data.NightModeInfo
import com.zacharee1.systemuituner.data.defaultNightTemp
import com.zacharee1.systemuituner.data.maxNightTemp
import com.zacharee1.systemuituner.data.minNightTemp
import com.zacharee1.systemuituner.databinding.NightModeBinding
import com.zacharee1.systemuituner.interfaces.IOptionDialogCallback
import com.zacharee1.systemuituner.util.PrefManager
import com.zacharee1.systemuituner.util.launch
import com.zacharee1.systemuituner.util.prefManager
import tk.zwander.seekbarpreference.SeekBarView

class NightModeView(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs), IOptionDialogCallback, SharedPreferences.OnSharedPreferenceChangeListener {
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

    override var callback: (suspend (data: Any?) -> Boolean)? = null
    private val nightModeInfo = NightModeInfo()

    private val binding by lazy { NightModeBinding.bind(this) }

    override fun onFinishInflate() {
        super.onFinishInflate()

        val atLeastNMR1 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1

        binding.nightDisplayWrapper.isVisible = isInEditMode || atLeastNMR1
        binding.twilightWrapper.isVisible = isInEditMode || !atLeastNMR1
        binding.resetNightMode.setOnClickListener {
            launch {
                nightModeInfo.nullAll()
                callback?.invoke(nightModeInfo)
                updateStates()
            }
        }

        updateStates()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        context.prefManager.prefs.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        context.prefManager.prefs.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == PrefManager.SAVED_OPTIONS) {
            post {
                updateStates()
            }
        }
    }

    private fun updateStates() {
        val atLeastNMR1 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1

        nightModeInfo.populate(context)

        if (atLeastNMR1) {
            binding.nightDisplayEnabled.isChecked = nightModeInfo.nightModeActivated == 1
            binding.nightDisplayEnabled.setOnCheckedChangeListener { _, isChecked ->
                val newValue = if (isChecked) 1 else 0

                if (newValue != nightModeInfo.nightModeActivated) {
                    nightModeInfo.nightModeActivated = newValue

                    launch {
                        if (callback?.invoke(nightModeInfo) == false) {
                            nightModeInfo.nightModeActivated = if (isChecked) 0 else 1
                            binding.nightDisplayEnabled.isChecked = !isChecked
                        }
                    }
                }
            }

            binding.nightDisplayAuto.isChecked = nightModeInfo.nightModeAuto == 1
            binding.nightDisplayAuto.setOnCheckedChangeListener { _, isChecked ->
                val newValue = if (isChecked) 1 else 0

                if (newValue != nightModeInfo.nightModeAuto) {
                    nightModeInfo.nightModeAuto = newValue

                    launch {
                        if (callback?.invoke(nightModeInfo) == false) {
                            nightModeInfo.nightModeAuto = if (isChecked) 0 else 1
                            binding.nightDisplayAuto.isChecked = !isChecked
                        }
                    }
                }
            }

            binding.nightDisplayTemp.onBind(
                minValue = context.minNightTemp,
                maxValue = context.maxNightTemp,
                progress = nightModeInfo.nightModeTemp ?: 5000,
                defaultValue = context.defaultNightTemp,
                scale = 1f,
                units = null,
                key = "",
                listener = object : SeekBarView.SeekBarListener {
                    override fun onProgressAdded() {}
                    override fun onProgressReset() {}
                    override fun onProgressSubtracted() {}
                    override fun onProgressChanged(newValue: Int, newScaledValue: Float) {
                        if (newScaledValue.toInt() != nightModeInfo.nightModeTemp) {
                            launch {
                                val oldValue = nightModeInfo.nightModeTemp

                                nightModeInfo.nightModeTemp = newScaledValue.toInt()
                                if (callback?.invoke(nightModeInfo) == false) {
                                    nightModeInfo.nightModeTemp = oldValue
                                    binding.nightDisplayTemp.scaledProgress = nightModeInfo.nightModeTemp?.toFloat() ?: 5000f
                                }
                            }
                        }
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
                    if ((nightModeInfo.twilightMode ?: TWILIGHT_OFF) != position) {
                        launch {
                            val oldValue = nightModeInfo.twilightMode

                            nightModeInfo.twilightMode = position
                            if (callback?.invoke(nightModeInfo) == false) {
                                nightModeInfo.twilightMode = oldValue
                            }
                        }
                    }
                }
            }
        }
    }
}