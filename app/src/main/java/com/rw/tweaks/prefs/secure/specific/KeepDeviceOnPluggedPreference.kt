package com.rw.tweaks.prefs.secure.specific

import android.content.Context
import android.provider.Settings
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import androidx.preference.DialogPreference
import com.rw.tweaks.R
import com.rw.tweaks.util.ISecurePreference
import com.rw.tweaks.util.ISpecificPreference
import com.rw.tweaks.util.SecurePreference
import com.rw.tweaks.util.SettingsType

class KeepDeviceOnPluggedPreference(context: Context, attrs: AttributeSet) : DialogPreference(context, attrs), ISecurePreference by SecurePreference(context), ISpecificPreference {
    override var type: SettingsType = SettingsType.GLOBAL
    override val keys: Array<String> = arrayOf(Settings.Global.STAY_ON_WHILE_PLUGGED_IN)

    init {
        key = "keep_device_on"

        setTitle(R.string.feature_keep_screen_on)
        setSummary(R.string.feature_keep_screen_on_desc)

        dialogTitle = title
        dialogMessage = summary
        setIcon(R.drawable.ic_baseline_visibility_24)
        iconColor = ContextCompat.getColor(context, R.color.pref_color_2)

        init(this)
    }
}