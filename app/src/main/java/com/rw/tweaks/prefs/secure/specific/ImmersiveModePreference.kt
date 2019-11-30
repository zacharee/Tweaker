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

class ImmersiveModePreference(context: Context, attrs: AttributeSet) : DialogPreference(context, attrs), ISecurePreference by SecurePreference(context), ISpecificPreference {
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

        init(this)
    }
}