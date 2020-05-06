package com.zacharee1.systemuituner.interfaces

import android.content.Context
import android.content.ContextWrapper
import android.util.AttributeSet
import androidx.preference.Preference
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.util.SettingsType
import com.zacharee1.systemuituner.util.api
import com.zacharee1.systemuituner.util.apiToName
import com.zacharee1.systemuituner.util.verifiers.BasePreferenceEnabledVerifier
import com.zacharee1.systemuituner.util.verifiers.BaseVisibilityVerifier

interface ISecurePreference : IDangerousPreference {
    var type: SettingsType
    var writeKey: String?

    fun initSecure(pref: Preference) {
        if (writeKey == null) writeKey = pref.key
    }
}

class SecurePreference(context: Context, attrs: AttributeSet?) :
    ISecurePreference, IDangerousPreference by DangerousPreference(context, attrs) {
    override var type: SettingsType =
        SettingsType.UNDEFINED
    override var writeKey: String? = null

    init {
        if (attrs != null) {
            val array = context.theme.obtainStyledAttributes(attrs, R.styleable.BaseSecurePreference, 0, 0)

            type = SettingsType.values().find { it.value ==  array.getInt(R.styleable.BaseSecurePreference_settings_type, SettingsType.UNDEFINED.value)} ?: SettingsType.UNDEFINED
            writeKey = array.getString(R.styleable.BaseSecurePreference_differing_key)

            array.recycle()
        }
    }
}