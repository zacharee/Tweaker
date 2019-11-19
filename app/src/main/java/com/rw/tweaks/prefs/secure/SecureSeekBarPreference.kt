package com.rw.tweaks.prefs.secure

import android.content.Context
import android.util.AttributeSet
import androidx.preference.DialogPreference
import com.rw.tweaks.R
import com.rw.tweaks.util.ISecurePreference
import com.rw.tweaks.util.SecurePreference
import com.rw.tweaks.util.SettingsType
import com.rw.tweaks.util.verifiers.BaseVisibilityVerifier

class SecureSeekBarPreference(context: Context, attrs: AttributeSet) : DialogPreference(context, attrs), ISecurePreference by SecurePreference(context) {
    var minValue: Int = 0
    var maxValue: Int = 100
    var defaultValue = 0
    var scale = 1.0f
    var units: String? = null

    init {
        val array = context.theme.obtainStyledAttributes(attrs, R.styleable.SecureSeekBarPreference, 0, 0)
        val android = context.theme.obtainStyledAttributes(attrs, R.styleable.Preference, 0, 0)

        type = SettingsType.values().find { it.value == array.getInt(R.styleable.SecureSeekBarPreference_settings_type, SettingsType.UNDEFINED.value) } ?: SettingsType.UNDEFINED
        writeKey = array.getString(R.styleable.SecureSeekBarPreference_differing_key)
        minValue = array.getInt(R.styleable.SecureSeekBarPreference_minValue, minValue)
        maxValue = array.getInt(R.styleable.SecureSeekBarPreference_maxValue, maxValue)
        defaultValue = android.getInt(R.styleable.Preference_android_defaultValue, defaultValue)
        scale = array.getFloat(R.styleable.SecureSeekBarPreference_scale, scale)
        units = array.getString(R.styleable.SecureSeekBarPreference_units)
        dangerous = array.getBoolean(R.styleable.SecureSeekBarPreference_dangerous, false)
        lowApi = array.getInt(R.styleable.SecureSeekBarPreference_low_api, lowApi)
        highApi = array.getInt(R.styleable.SecureSeekBarPreference_high_api, highApi)

        dialogMessage = summary

        val clazz = array.getString(R.styleable.SecureSeekBarPreference_visibility_verifier)
        if (clazz != null) {
            visibilityVerifier = context.classLoader.loadClass(clazz)
                .getConstructor(Context::class.java)
                .newInstance(context) as BaseVisibilityVerifier
        }

        array.recycle()
        android.recycle()

        init(this)
    }
}