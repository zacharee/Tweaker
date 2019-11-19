package com.rw.tweaks.prefs.secure

import android.content.Context
import android.util.AttributeSet
import androidx.preference.ListPreference
import androidx.preference.Preference
import com.rw.tweaks.R
import com.rw.tweaks.util.*
import com.rw.tweaks.util.verifiers.BaseListPreferenceVerifier
import com.rw.tweaks.util.verifiers.BaseVisibilityVerifier

class SecureListPreference(context: Context, attrs: AttributeSet) : Preference.OnPreferenceChangeListener, ListPreference(context, attrs), ISecurePreference by SecurePreference(context) {
    private var verifier: BaseListPreferenceVerifier? = null
    private var _onPreferenceChangeListener: OnPreferenceChangeListener? = null

    init {
        val array = context.theme.obtainStyledAttributes(attrs, R.styleable.SecureListPreference, 0, 0)

        type = SettingsType.values().find { it.value ==  array.getInt(R.styleable.SecureListPreference_settings_type, SettingsType.UNDEFINED.value)} ?: SettingsType.UNDEFINED
        writeKey = array.getString(R.styleable.SecureListPreference_differing_key)
        dangerous = array.getBoolean(R.styleable.SecureListPreference_dangerous, false)
        lowApi = array.getInt(R.styleable.SecureListPreference_low_api, lowApi)
        highApi = array.getInt(R.styleable.SecureListPreference_high_api, highApi)

        array.getString(R.styleable.SecureListPreference_verifier)?.let {
            verifier = context.classLoader.loadClass(it)
                .getConstructor(Context::class.java)
                .newInstance(context) as BaseListPreferenceVerifier

            verifier!!.verifyEntries(entries, entryValues).apply {
                entries = first
                entryValues = second
            }
        }

        val clazz = array.getString(R.styleable.SecureListPreference_visibility_verifier)
        if (clazz != null) {
            visibilityVerifier = context.classLoader.loadClass(clazz)
                .getConstructor(Context::class.java)
                .newInstance(context) as BaseVisibilityVerifier
        }

        array.recycle()

        dialogMessage = summary

        super.setOnPreferenceChangeListener(this)
        init(this)
    }

    override fun onSetInitialValue(defaultValue: Any?) {
        value = context.getSetting(type, key) ?: defaultValue?.toString() ?: entryValues[0].toString()
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
            context.writeSetting(type, writeKey, newValue.toString().toInt())
        }

        return update
    }
}