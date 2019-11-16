package com.rw.tweaks.prefs.secure.specific

import android.content.Context
import android.util.AttributeSet
import androidx.preference.DialogPreference
import com.rw.tweaks.R
import com.rw.tweaks.util.ISecurePreference
import com.rw.tweaks.util.SettingsType

class ImmersiveModePreference(context: Context, attrs: AttributeSet) : DialogPreference(context, attrs), ISecurePreference {
    override var type: SettingsType = SettingsType.UNDEFINED
    override var writeKey: String? = null
    override var dangerous: Boolean = false

    init {
        key = "immersive_mode_pref"

        setTitle(R.string.feature_immersive_mode)
        setSummary(R.string.feature_immersive_mode_desc)

        dialogTitle = title
        dialogMessage = summary
    }

    override fun onValueChanged(newValue: Any?, key: String?) {}
}