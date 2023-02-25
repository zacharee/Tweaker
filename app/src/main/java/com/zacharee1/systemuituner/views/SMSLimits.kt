package com.zacharee1.systemuituner.views

import android.content.Context
import android.provider.Settings
import android.util.AttributeSet
import android.widget.ScrollView
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.zacharee1.systemuituner.data.SettingsType
import com.zacharee1.systemuituner.databinding.SmsLimitsBinding
import com.zacharee1.systemuituner.util.*
import kotlinx.coroutines.launch

class SMSLimits(context: Context, attrs: AttributeSet) : ScrollView(context, attrs) {
    companion object {
        private const val COUNT_DEF = 30
        private const val INTERVAL_DEF = 1800000
    }

    private val binding by lazy { SmsLimitsBinding.bind(this) }

    override fun onFinishInflate() {
        super.onFinishInflate()

        val scope = findViewTreeLifecycleOwner()?.lifecycleScope

        binding.maxCount.editText?.setText(context.getSetting(SettingsType.GLOBAL, Settings.Global.SMS_OUTGOING_CHECK_MAX_COUNT, COUNT_DEF))
        binding.interval.editText?.setText(context.getSetting(SettingsType.GLOBAL, Settings.Global.SMS_OUTGOING_CHECK_INTERVAL_MS, INTERVAL_DEF))

        binding.maxCount.setStartIconOnClickListener {
            scope?.launch {
                context.writeSetting(SettingsType.GLOBAL, Settings.Global.SMS_OUTGOING_CHECK_MAX_COUNT, COUNT_DEF, saveOption = true)
            }
            binding.maxCount.editText?.setText(COUNT_DEF.toString())
        }

        binding.interval.setStartIconOnClickListener {
            scope?.launch {
                context.writeSetting(SettingsType.GLOBAL, Settings.Global.SMS_OUTGOING_CHECK_INTERVAL_MS, INTERVAL_DEF, saveOption = true)
            }
            binding.interval.editText?.setText(INTERVAL_DEF.toString())
        }

        binding.maxCount.setEndIconOnClickListener {
            val c = binding.maxCount.editText?.text?.toString()?.toIntOrNullOnError() ?: COUNT_DEF

            scope?.launch {
                context.writeSetting(SettingsType.GLOBAL, Settings.Global.SMS_OUTGOING_CHECK_MAX_COUNT, c, saveOption = true)
            }
        }

        binding.interval.setEndIconOnClickListener {
            val i = binding.interval.editText?.text?.toString()?.toIntOrNullOnError() ?: INTERVAL_DEF

            scope?.launch {
                context.writeSetting(SettingsType.GLOBAL, Settings.Global.SMS_OUTGOING_CHECK_INTERVAL_MS, i, saveOption = true)
            }
        }
    }
}