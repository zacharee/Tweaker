package com.rw.tweaks.prefs.specific

import android.content.Context
import android.util.AttributeSet
import androidx.preference.DialogPreference
import com.rw.tweaks.R
import com.rw.tweaks.util.ISecurePreference
import com.rw.tweaks.util.SettingsType

class KeepDeviceOnPluggedPreference(context: Context, attrs: AttributeSet) : DialogPreference(context, attrs), ISecurePreference {
    override var type: SettingsType
        get() = SettingsType.UNDEFINED
        set(value) {}
    override var writeKey: String?
        get() = null
        set(value) {}
    override var dangerous = false

    init {
        key = "keep_device_on"

        setTitle(R.string.feature_keep_screen_on)
        setSummary(R.string.feature_keep_screen_on_desc)

        dialogTitle = title
        dialogMessage = summary
    }

    override fun onValueChanged(newValue: Any?, key: String?) {}
}