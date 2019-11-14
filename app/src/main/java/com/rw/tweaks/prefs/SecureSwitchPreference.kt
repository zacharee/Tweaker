package com.rw.tweaks.prefs

import android.content.Context
import android.util.AttributeSet
import androidx.preference.DialogPreference
import com.rw.tweaks.R
import com.rw.tweaks.util.ISecurePreference
import com.rw.tweaks.util.SettingsType

class SecureSwitchPreference(context: Context, attrs: AttributeSet) : DialogPreference(context, attrs), ISecurePreference {
    companion object {
        const val DEFAULT_ENABLED = "1"
        const val DEFAULT_DISABLED = "0"
    }

    override var type = SettingsType.UNDEFINED
    var enabled = DEFAULT_ENABLED
    var disabled = DEFAULT_DISABLED
    override var writeKey: String? = null
        get() = field ?: key
    override var dangerous = false

    init {
        isPersistent = false
        val array = context.theme.obtainStyledAttributes(attrs, R.styleable.SecureSwitchPreference, 0, 0)

        type = SettingsType.values().find { it.value == array.getInt(R.styleable.SecureSwitchPreference_settings_type, SettingsType.UNDEFINED.value) } ?: SettingsType.UNDEFINED
        enabled = array.getString(R.styleable.SecureSwitchPreference_enabled_value) ?: DEFAULT_ENABLED
        disabled = array.getString(R.styleable.SecureSwitchPreference_disabled_value) ?: DEFAULT_DISABLED
        writeKey = array.getString(R.styleable.SecureSwitchPreference_differing_key)
        dangerous = array.getBoolean(R.styleable.SecureSwitchPreference_dangerous, false)

        dialogMessage = summary
    }

    override fun onValueChanged(newValue: Any?, key: String?) {}
}