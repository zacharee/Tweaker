package com.rw.tweaks.prefs.secure

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import com.rw.tweaks.R
import com.rw.tweaks.util.*
import com.rw.tweaks.util.verifiers.BaseVisibilityVerifier

class SecureEditTextPreference(context: Context, attrs: AttributeSet) : EditTextPreference(context, attrs), Preference.OnPreferenceChangeListener, ISecurePreference by SecurePreference() {
    private var inputType: Int = InputType.TYPE_CLASS_TEXT

    private var _onPreferenceChangeListener: OnPreferenceChangeListener? = null

    init {
        val array = context.theme.obtainStyledAttributes(attrs, R.styleable.SecureEditTextPreference, 0, 0)

        type = SettingsType.values().find { it.value ==  array.getInt(R.styleable.SecureEditTextPreference_settings_type, SettingsType.UNDEFINED.value)} ?: SettingsType.UNDEFINED
        writeKey = array.getString(R.styleable.SecureEditTextPreference_differing_key)
        dangerous = array.getBoolean(R.styleable.SecureEditTextPreference_dangerous, false)
        inputType = array.getInt(R.styleable.SecureEditTextPreference_android_inputType, inputType)
        lowApi = array.getInt(R.styleable.SecureEditTextPreference_low_api, lowApi)
        highApi = array.getInt(R.styleable.SecureEditTextPreference_high_api, highApi)

        val clazz = array.getString(R.styleable.SecureEditTextPreference_visibility_verifier)
        if (clazz != null) {
            visibilityVerifier = context.classLoader.loadClass(clazz)
                .getConstructor(Context::class.java)
                .newInstance(context) as BaseVisibilityVerifier
        }

        dialogMessage = summary

        setOnBindEditTextListener {
            it.inputType = inputType
        }

        array.recycle()

        super.setOnPreferenceChangeListener(this)
        init(this)
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
}