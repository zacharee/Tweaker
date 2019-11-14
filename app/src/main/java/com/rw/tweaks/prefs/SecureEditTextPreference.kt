package com.rw.tweaks.prefs

import android.content.Context
import android.util.AttributeSet
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import com.rw.tweaks.R
import com.rw.tweaks.util.SettingsType
import com.rw.tweaks.util.getSetting
import com.rw.tweaks.util.writeSetting

class SecureEditTextPreference(context: Context, attrs: AttributeSet) : EditTextPreference(context, attrs), Preference.OnPreferenceChangeListener {
    var type = SettingsType.UNDEFINED
    var writeKey: String? = null
        get() = field ?: key

    private var _onPreferenceChangeListener: OnPreferenceChangeListener? = null

    init {
        val array = context.theme.obtainStyledAttributes(attrs, R.styleable.SecureEditTextPreference, 0, 0)

        type = SettingsType.values().find { it.value ==  array.getInt(R.styleable.SecureEditTextPreference_settings_type, SettingsType.UNDEFINED.value)} ?: SettingsType.UNDEFINED
        writeKey = array.getString(R.styleable.SecureEditTextPreference_differing_key)

        dialogMessage = summary

        super.setOnPreferenceChangeListener(this)
    }

    override fun onSetInitialValue(defaultValue: Any?) {
        this.text = context.getSetting(type, writeKey)
    }

    override fun setOnPreferenceChangeListener(onPreferenceChangeListener: OnPreferenceChangeListener?) {
        _onPreferenceChangeListener = onPreferenceChangeListener
    }

    override fun getOnPreferenceChangeListener(): OnPreferenceChangeListener? {
        return _onPreferenceChangeListener
    }

    override fun onPreferenceChange(preference: Preference, newValue: Any): Boolean {
        val update = _onPreferenceChangeListener?.onPreferenceChange(preference, newValue) ?: true

        if (update) {
            context.writeSetting(type, writeKey, newValue.toString().toBoolean())
        }

        return update
    }
}