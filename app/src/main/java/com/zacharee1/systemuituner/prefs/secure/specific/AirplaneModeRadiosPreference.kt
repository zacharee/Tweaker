package com.zacharee1.systemuituner.prefs.secure.specific

import android.content.Context
import android.provider.Settings
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.data.AirplaneModeRadiosData
import com.zacharee1.systemuituner.interfaces.ISpecificPreference
import com.zacharee1.systemuituner.prefs.base.BaseDialogPreference
import com.zacharee1.systemuituner.data.SettingsType
import com.zacharee1.systemuituner.util.prefManager
import com.zacharee1.systemuituner.util.writeSetting

class AirplaneModeRadiosPreference(context: Context, attrs: AttributeSet) : BaseDialogPreference(context, attrs),
    ISpecificPreference {
    override val keys = hashMapOf(
        SettingsType.GLOBAL to arrayOf(
            Settings.Global.AIRPLANE_MODE_RADIOS,
            Settings.Global.AIRPLANE_MODE_TOGGLEABLE_RADIOS
        )
    )

    init {
        key = "airplane_mode_radios"

        setTitle(R.string.special_sub_airplane_mode)
        setSummary(R.string.special_sub_airplane_mode_desc)

        dialogTitle = title
        dialogMessage = summary
        setIcon(R.drawable.ic_baseline_airplanemode_active_24)
        iconColor = ContextCompat.getColor(context, R.color.pref_color_4)
    }

    override fun onValueChanged(newValue: Any?, key: String) {
        val data = newValue as AirplaneModeRadiosData?

        context.prefManager.apply {
            saveOption(SettingsType.GLOBAL, Settings.Global.AIRPLANE_MODE_RADIOS, data?.blacklisted)
            saveOption(SettingsType.GLOBAL, Settings.Global.AIRPLANE_MODE_TOGGLEABLE_RADIOS, data?.toggleable)
        }
        context.writeSetting(SettingsType.GLOBAL, Settings.Global.AIRPLANE_MODE_RADIOS, data?.blacklisted)
        context.writeSetting(SettingsType.GLOBAL, Settings.Global.AIRPLANE_MODE_TOGGLEABLE_RADIOS, data?.toggleable)
    }
}