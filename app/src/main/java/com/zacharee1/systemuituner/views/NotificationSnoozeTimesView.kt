package com.zacharee1.systemuituner.views

import android.content.Context
import android.util.AttributeSet
import android.widget.EditText
import com.zacharee1.systemuituner.interfaces.IOptionDialogCallback
import com.zacharee1.systemuituner.util.SettingsType
import com.zacharee1.systemuituner.util.getSetting
import kotlinx.android.synthetic.main.notification_snooze_times.view.*
import kotlin.collections.HashMap

class NotificationSnoozeTimesView(context: Context, attrs: AttributeSet) :
    RoundedFrameCardView(context, attrs), IOptionDialogCallback {
    override var callback: ((data: Any?) -> Unit)? = null

    override fun onFinishInflate() {
        super.onFinishInflate()

        val setting = context.getSetting(SettingsType.GLOBAL, "notification_snooze_options")

        var defTime = "60"
        var aTime = "15"
        var bTime = "30"
        var cTime = "60"
        var dTime = "120"

        if (!setting.isNullOrBlank()) {
            try {
                val parts = setting.split(",")
                val default = parts[0].split("=")[1]
                val options = parts[1].split("=")[1].split(":")

                defTime = default
                aTime = options[0]
                bTime = options[1]
                cTime = options[2]
                dTime = options[3]
            } catch (e: IndexOutOfBoundsException) {
                defTime = "60"
                aTime = "15"
                bTime = "30"
                cTime = "60"
                dTime = "120"
            }
        }

        snooze_default.setText(defTime)
        snooze_a.setText(aTime)
        snooze_b.setText(bTime)
        snooze_c.setText(cTime)
        snooze_d.setText(dTime)

        apply.setOnClickListener {
            callback?.invoke(
                StringBuilder()
                    .append("default=")
                    .append(snooze_default.textOrDefault(defTime))
                    .append(",")
                    .append("options_array=")
                    .append(snooze_a.textOrDefault(aTime))
                    .append(":")
                    .append(snooze_b.textOrDefault(bTime))
                    .append(":")
                    .append(snooze_c.textOrDefault(cTime))
                    .append(":")
                    .append(snooze_d.textOrDefault(dTime))
                    .toString()
            )
        }
    }

    fun EditText.textOrDefault(default: String): String {
        return text?.toString().run { if (this.isNullOrBlank()) default else this }
    }
}