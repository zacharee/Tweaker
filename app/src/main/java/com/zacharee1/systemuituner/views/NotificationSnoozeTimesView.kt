package com.zacharee1.systemuituner.views

import android.content.Context
import android.util.AttributeSet
import android.widget.EditText
import android.widget.FrameLayout
import com.zacharee1.systemuituner.databinding.NotificationSnoozeTimesBinding
import com.zacharee1.systemuituner.interfaces.IOptionDialogCallback
import com.zacharee1.systemuituner.data.SettingsType
import com.zacharee1.systemuituner.util.getSetting
import com.zacharee1.systemuituner.util.launch

class NotificationSnoozeTimesView(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs), IOptionDialogCallback {
    override var callback: (suspend (data: Any?) -> Boolean)? = null

    private val binding by lazy { NotificationSnoozeTimesBinding.bind(this) }

    override fun onFinishInflate() {
        super.onFinishInflate()

        init()
    }

    private fun init() {
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

        binding.snoozeDefault.setText(defTime)
        binding.snoozeA.setText(aTime)
        binding.snoozeB.setText(bTime)
        binding.snoozeC.setText(cTime)
        binding.snoozeD.setText(dTime)

        binding.apply.setOnClickListener {
            launch {
                if (callback?.invoke(
                        StringBuilder()
                            .append("default=")
                            .append(binding.snoozeDefault.textOrDefault(defTime))
                            .append(",")
                            .append("options_array=")
                            .append(binding.snoozeA.textOrDefault(aTime))
                            .append(":")
                            .append(binding.snoozeB.textOrDefault(bTime))
                            .append(":")
                            .append(binding.snoozeC.textOrDefault(cTime))
                            .append(":")
                            .append(binding.snoozeD.textOrDefault(dTime))
                            .toString()
                    ) == false) {
                    init()
                }
            }
        }
    }

    fun EditText.textOrDefault(default: String): String {
        return text?.toString().run { if (this.isNullOrBlank()) default else this }
    }
}