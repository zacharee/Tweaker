package com.zacharee1.systemuituner.prefs.secure

import android.content.Context
import android.util.AttributeSet
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.prefs.secure.base.BaseSecurePreference
import com.zacharee1.systemuituner.util.prefManager
import com.zacharee1.systemuituner.util.writeSetting

class SecureSeekBarPreference(context: Context, attrs: AttributeSet) : BaseSecurePreference(context, attrs) {
    var minValue: Int = 0
    var maxValue: Int = 100
    var defaultValue = 0
    var scale = 1.0f
    var units: String? = null

    init {
        val array = context.theme.obtainStyledAttributes(attrs, R.styleable.SecureSeekBarPreference, 0, 0)
        val android = context.theme.obtainStyledAttributes(attrs, R.styleable.Preference, 0, 0)

        minValue = array.getInt(R.styleable.SecureSeekBarPreference_minValue, minValue)
        maxValue = array.getInt(R.styleable.SecureSeekBarPreference_maxValue, maxValue)
        defaultValue = android.getInt(R.styleable.Preference_android_defaultValue, defaultValue)
        scale = array.getFloat(R.styleable.SecureSeekBarPreference_scale, scale)
        units = array.getString(R.styleable.SecureSeekBarPreference_units)

        array.recycle()
        android.recycle()
    }

    override fun onValueChanged(newValue: Any?, key: String) {
        context.prefManager.saveOption(type, writeKey, newValue)
        context.writeSetting(type, writeKey, newValue, dangerous)
    }
}