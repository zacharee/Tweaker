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

        val times = HashMap<String, String>()
        val setting = context.getSetting(SettingsType.GLOBAL, "notification_snooze_options")

        if (setting.isNullOrBlank()) {
            times["default"] = "60"
            times["a"] = "15"
            times["b"] = "30"
            times["c"] = "60"
            times["d"] = "120"
        } else {
            val parts = setting.split(",")
            val default = parts[0].split("=")[1]
            val options = parts[1].split("=")[1].split(":")

            times["default"] = default
            times["a"] = options[0]
            times["b"] = options[1]
            times["c"] = options[2]
            times["d"] = options[3]
        }

        snooze_default.setText(times["default"])
        snooze_a.setText(times["a"])
        snooze_b.setText(times["b"])
        snooze_c.setText(times["c"])
        snooze_d.setText(times["d"])

        apply.setOnClickListener {
            callback?.invoke(
                "default=${snooze_default.textOrDefault(times["default"]!!)}," +
                        "options_array=${snooze_a.textOrDefault(times["a"]!!)}:${snooze_b.textOrDefault(
                            times["b"]!!
                        )}:${snooze_c.textOrDefault(times["c"]!!)}:${snooze_d.textOrDefault(times["d"]!!)}"
            )
        }
    }

    fun EditText.textOrDefault(default: String): String {
        return text?.toString().run { if (this.isNullOrBlank()) default else this }
    }
}