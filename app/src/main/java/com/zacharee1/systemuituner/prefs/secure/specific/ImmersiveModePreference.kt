package com.zacharee1.systemuituner.prefs.secure.specific

import android.content.Context
import android.provider.Settings
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.prefs.secure.base.BaseSecurePreference
import com.zacharee1.systemuituner.util.ISpecificPreference
import com.zacharee1.systemuituner.util.SettingsType

class ImmersiveModePreference(context: Context, attrs: AttributeSet) : BaseSecurePreference(context, attrs), ISpecificPreference {
    override var type: SettingsType = SettingsType.GLOBAL
    override val keys: Array<String> = arrayOf(Settings.Global.POLICY_CONTROL)

    init {
        key = "immersive_mode_pref"

        setTitle(R.string.feature_immersive_mode)
        setSummary(R.string.feature_immersive_mode_desc)

        dialogTitle = title
        dialogMessage = summary
        setIcon(R.drawable.ic_baseline_fullscreen_24)
        iconColor = ContextCompat.getColor(context, R.color.pref_color_4)
    }
}