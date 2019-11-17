package com.rw.tweaks.prefs.secure

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import com.rw.tweaks.R
import com.rw.tweaks.util.ISecurePreference
import com.rw.tweaks.util.SettingsType
import com.rw.tweaks.util.getSetting
import com.rw.tweaks.util.writeSetting

class SecureEditTextPreference(context: Context, attrs: AttributeSet) : EditTextPreference(context, attrs), Preference.OnPreferenceChangeListener, ISecurePreference {
    override var type = SettingsType.UNDEFINED
    override var writeKey: String? = null
        get() = field ?: key
    override var dangerous = false
    private var inputType: Int = InputType.TYPE_CLASS_TEXT

    private var _onPreferenceChangeListener: OnPreferenceChangeListener? = null

    init {
        val array = context.theme.obtainStyledAttributes(attrs, R.styleable.SecureEditTextPreference, 0, 0)

        type = SettingsType.values().find { it.value ==  array.getInt(R.styleable.SecureEditTextPreference_settings_type, SettingsType.UNDEFINED.value)} ?: SettingsType.UNDEFINED
        writeKey = array.getString(R.styleable.SecureEditTextPreference_differing_key)
        dangerous = array.getBoolean(R.styleable.SecureEditTextPreference_dangerous, false)
        inputType = array.getInt(R.styleable.SecureEditTextPreference_android_inputType, inputType)

        dialogMessage = summary

        setOnBindEditTextListener {
            it.inputType = inputType
        }

        array.recycle()

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
            context.writeSetting(type, writeKey, newValue.toString())
        }

        return update
    }

    override fun onValueChanged(newValue: Any?, key: String?) {}
}