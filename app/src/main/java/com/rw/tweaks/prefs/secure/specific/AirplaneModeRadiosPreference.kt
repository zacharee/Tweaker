package com.rw.tweaks.prefs.secure.specific

import android.content.Context
import android.util.AttributeSet
import androidx.preference.DialogPreference
import com.rw.tweaks.R
import com.rw.tweaks.util.ISecurePreference
import com.rw.tweaks.util.SettingsType

class AirplaneModeRadiosPreference(context: Context, attrs: AttributeSet) : DialogPreference(context, attrs), ISecurePreference {
    override var type: SettingsType
        get() = SettingsType.UNDEFINED
        set(value) {}
    override var writeKey: String?
        get() = null
        set(value) {}
    override var dangerous = false

    init {
        key = "airplane_mode_radios"

        setTitle(R.string.special_sub_airplane_mode)
        setSummary(R.string.special_sub_airplane_mode_desc)

        dialogTitle = title
        dialogMessage = summary
    }

    override fun onValueChanged(newValue: Any?, key: String?) {}
}