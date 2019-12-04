package com.rw.tweaks.prefs.secure.specific

import android.content.Context
import android.provider.Settings
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.rw.tweaks.R
import com.rw.tweaks.prefs.secure.base.BaseSecurePreference
import com.rw.tweaks.util.ISpecificPreference
import com.rw.tweaks.util.SettingsType

class AirplaneModeRadiosPreference(context: Context, attrs: AttributeSet) : BaseSecurePreference(context, attrs), ISpecificPreference {
    override var type: SettingsType = SettingsType.GLOBAL
    override val keys: Array<String> = arrayOf(Settings.Global.AIRPLANE_MODE_RADIOS, Settings.Global.AIRPLANE_MODE_TOGGLEABLE_RADIOS)

    init {
        key = "airplane_mode_radios"

        setTitle(R.string.special_sub_airplane_mode)
        setSummary(R.string.special_sub_airplane_mode_desc)

        dialogTitle = title
        dialogMessage = summary
        setIcon(R.drawable.ic_baseline_airplanemode_active_24)
        iconColor = ContextCompat.getColor(context, R.color.pref_color_4)
    }
}