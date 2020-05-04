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

interface ISecurePreference : IVerifierPreference, IDangerousPreference {
    var type: SettingsType
    var writeKey: String?
}

class SecurePreference(context: Context, attrs: AttributeSet?) :
    ISecurePreference, VerifierPreference(context, attrs), IDangerousPreference by DangerousPreference(context, attrs) {
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

    override fun init(pref: Preference) {
        super.init(pref)
        if (writeKey == null) writeKey = pref.key
    }
}