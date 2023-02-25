package com.zacharee1.systemuituner.views

import android.content.Context
import android.provider.Settings
import android.util.AttributeSet
import android.widget.ScrollView
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.zacharee1.systemuituner.data.SettingsType
import com.zacharee1.systemuituner.databinding.StorageThresholdsBinding
import com.zacharee1.systemuituner.util.*
import kotlinx.coroutines.launch

class StorageThresholds(context: Context, attrs: AttributeSet) : ScrollView(context, attrs) {
    private val binding by lazy { StorageThresholdsBinding.bind(this) }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        val scope = findViewTreeLifecycleOwner()?.lifecycleScope

        binding.thresholdPercent.apply {
            scaledProgress = context.getSetting(SettingsType.GLOBAL, Settings.Global.SYS_STORAGE_THRESHOLD_PERCENTAGE, 5f)?.toFloatOrNull() ?: 5f
            listener = object : SimpleSeekBarListener() {
                override fun onProgressChanged(newValue: Int, newScaledValue: Float) {
                    scope?.launch {
                        context.writeSetting(SettingsType.GLOBAL, Settings.Global.SYS_STORAGE_THRESHOLD_PERCENTAGE, newScaledValue.toInt(), saveOption = true)
                    }
                }
            }
        }

        binding.thresholdBytes.apply {
            scaledProgress = (context.getSetting(SettingsType.GLOBAL, Settings.Global.SYS_STORAGE_THRESHOLD_MAX_BYTES, 500000000f)?.toFloatOrNull() ?: 500000000f) * scale
            listener = object : SimpleSeekBarListener() {
                override fun onProgressChanged(newValue: Int, newScaledValue: Float) {
                    scope?.launch {
                        context.writeSetting(SettingsType.GLOBAL, Settings.Global.SYS_STORAGE_THRESHOLD_MAX_BYTES, newValue, saveOption = true)
                    }
                }
            }
        }
    }
}