package com.zacharee1.systemuituner.prefs.demo

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.util.Log
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.prefs.demo.base.BaseDemoPreference
import com.zacharee1.systemuituner.prefs.secure.base.BaseSecurePreference

class DemoSeekBarPreference(context: Context, attrs: AttributeSet) : BaseDemoPreference(context, attrs) {
    var minValue: Int = 0
    var maxValue: Int = 100
    var defaultValue = 0
    var scale = 1.0f
    var units: String? = null
    override var writeKey: String? = null
        get() = key

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

    override fun onAttachedToHierarchy(preferenceManager: PreferenceManager?) {
        super.onAttachedToHierarchy(preferenceManager)
        val prefValue = sharedPreferences.all[key]?.toString()?.toFloat()

        summary = prefValue?.toString() ?: (this.defaultValue * scale).toString()
    }

    override fun onValueChanged(newValue: Any?, key: String) {
        val value = newValue?.toString()?.toFloat()

        sharedPreferences.edit {
            if (value != null) {
                putFloat(writeKey!!, value)
            } else {
                remove(writeKey!!)
            }
        }

        summary = value.toString()
    }
}