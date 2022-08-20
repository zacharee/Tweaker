package com.zacharee1.systemuituner.prefs.secure.specific

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.interfaces.ISpecificPreference
import com.zacharee1.systemuituner.prefs.base.BaseDialogPreference
import com.zacharee1.systemuituner.data.SettingsType
import com.zacharee1.systemuituner.util.prefManager
import com.zacharee1.systemuituner.util.writeSetting

class NotificationSnoozeTimesPreference(context: Context, attrs: AttributeSet) : BaseDialogPreference(context, attrs), ISpecificPreference {
    override val keys
        get() = hashMapOf(
            SettingsType.GLOBAL to arrayOf(key)
        )

    init {
        key = "notification_snooze_options"
        setTitle(R.string.option_custom_notification_snooze_times)
        setSummary(R.string.option_custom_notification_snooze_times_desc)

        dialogTitle = title
        dialogMessage = summary
        iconColor = context.resources.getColor(R.color.pref_color_5, context.theme)
        setIcon(R.drawable.ic_baseline_snooze_24)

        lowApi = Build.VERSION_CODES.O_MR1
    }

    override fun onValueChanged(newValue: Any?, key: String) {
        context.prefManager.saveOption(SettingsType.GLOBAL, "notification_snooze_options", newValue)
        context.writeSetting(SettingsType.GLOBAL, "notification_snooze_options", newValue)
    }
}