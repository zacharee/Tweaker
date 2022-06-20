package com.zacharee1.systemuituner.views

import android.content.Context
import android.provider.Settings
import android.util.AttributeSet
import android.widget.ScrollView
import com.zacharee1.systemuituner.data.SettingsType
import com.zacharee1.systemuituner.databinding.SmsLimitsBinding
import com.zacharee1.systemuituner.util.*

class SMSLimits(context: Context, attrs: AttributeSet) : ScrollView(context, attrs) {
    companion object {
        private const val COUNT_DEF = 30
        private const val INTERVAL_DEF = 1800000
    }

    private val binding by lazy { SmsLimitsBinding.bind(this) }

    override fun onFinishInflate() {
        super.onFinishInflate()

        binding.maxCount.editText?.setText(context.getSetting(SettingsType.GLOBAL, Settings.Global.SMS_OUTGOING_CHECK_MAX_COUNT, COUNT_DEF))
        binding.interval.editText?.setText(context.getSetting(SettingsType.GLOBAL, Settings.Global.SMS_OUTGOING_CHECK_INTERVAL_MS, INTERVAL_DEF))

        binding.maxCount.setStartIconOnClickListener {
            context.prefManager.saveOption(SettingsType.GLOBAL, Settings.Global.SMS_OUTGOING_CHECK_MAX_COUNT, COUNT_DEF)
            context.writeGlobal(Settings.Global.SMS_OUTGOING_CHECK_MAX_COUNT, COUNT_DEF)
            binding.maxCount.editText?.setText(COUNT_DEF.toString())
        }

        binding.interval.setStartIconOnClickListener {
            context.prefManager.saveOption(SettingsType.GLOBAL, Settings.Global.SMS_OUTGOING_CHECK_INTERVAL_MS, INTERVAL_DEF)
            context.writeGlobal(Settings.Global.SMS_OUTGOING_CHECK_INTERVAL_MS, INTERVAL_DEF)
            binding.interval.editText?.setText(INTERVAL_DEF.toString())
        }

        binding.maxCount.setEndIconOnClickListener {
            val c = binding.maxCount.editText?.text?.toString()?.toIntOrNullOnError() ?: COUNT_DEF

            context.prefManager.saveOption(SettingsType.GLOBAL, Settings.Global.SMS_OUTGOING_CHECK_MAX_COUNT, c)
            context.writeGlobal(Settings.Global.SMS_OUTGOING_CHECK_MAX_COUNT, c)
        }

        binding.interval.setEndIconOnClickListener {
            val i = binding.interval.editText?.text?.toString()?.toIntOrNullOnError() ?: INTERVAL_DEF

            context.prefManager.saveOption(SettingsType.GLOBAL, Settings.Global.SMS_OUTGOING_CHECK_INTERVAL_MS, i)
            context.writeGlobal(Settings.Global.SMS_OUTGOING_CHECK_INTERVAL_MS, i)
        }
    }
}