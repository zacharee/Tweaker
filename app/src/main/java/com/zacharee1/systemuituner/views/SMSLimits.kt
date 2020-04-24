package com.zacharee1.systemuituner.views

import android.content.Context
import android.provider.Settings
import android.util.AttributeSet
import android.widget.ScrollView
import com.zacharee1.systemuituner.util.SettingsType
import com.zacharee1.systemuituner.util.prefManager
import com.zacharee1.systemuituner.util.writeGlobal
import kotlinx.android.synthetic.main.sms_limits.view.*

class SMSLimits(context: Context, attrs: AttributeSet) : ScrollView(context, attrs) {
    companion object {
        private const val COUNT_DEF = 30
        private const val INTERVAL_DEF = 1800000
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        max_count.editText?.setText(Settings.Global.getInt(context.contentResolver, Settings.Global.SMS_OUTGOING_CHECK_MAX_COUNT, COUNT_DEF).toString())
        interval.editText?.setText(Settings.Global.getInt(context.contentResolver, Settings.Global.SMS_OUTGOING_CHECK_INTERVAL_MS, INTERVAL_DEF).toString())

        max_count.setStartIconOnClickListener {
            context.prefManager.saveOption(SettingsType.GLOBAL, Settings.Global.SMS_OUTGOING_CHECK_MAX_COUNT, COUNT_DEF)
            context.writeGlobal(Settings.Global.SMS_OUTGOING_CHECK_MAX_COUNT, COUNT_DEF)
            max_count.editText?.setText(COUNT_DEF.toString())
        }

        interval.setStartIconOnClickListener {
            context.prefManager.saveOption(SettingsType.GLOBAL, Settings.Global.SMS_OUTGOING_CHECK_INTERVAL_MS, INTERVAL_DEF)
            context.writeGlobal(Settings.Global.SMS_OUTGOING_CHECK_INTERVAL_MS, INTERVAL_DEF)
            interval.editText?.setText(INTERVAL_DEF.toString())
        }

        max_count.setEndIconOnClickListener {
            val c = max_count.editText?.text.run {
                when {
                    this == null -> COUNT_DEF
                    isBlank() -> COUNT_DEF
                    else -> this.toString().toInt()
                }
            }

            context.prefManager.saveOption(SettingsType.GLOBAL, Settings.Global.SMS_OUTGOING_CHECK_MAX_COUNT, c)
            context.writeGlobal(Settings.Global.SMS_OUTGOING_CHECK_MAX_COUNT, c)
        }

        interval.setEndIconOnClickListener {
            val i = interval.editText?.text.run {
                when {
                    this == null -> INTERVAL_DEF
                    isBlank() -> INTERVAL_DEF
                    else -> this.toString().toInt()
                }
            }

            context.prefManager.saveOption(SettingsType.GLOBAL, Settings.Global.SMS_OUTGOING_CHECK_INTERVAL_MS, i)
            context.writeGlobal(Settings.Global.SMS_OUTGOING_CHECK_INTERVAL_MS, i)
        }
    }
}