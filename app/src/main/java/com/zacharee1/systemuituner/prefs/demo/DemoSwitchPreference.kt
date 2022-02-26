package com.zacharee1.systemuituner.prefs.demo

import android.content.Context
import android.util.AttributeSet
import androidx.preference.PreferenceManager
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.prefs.demo.base.BaseDemoPreference

open class DemoSwitchPreference(context: Context, attrs: AttributeSet) : BaseDemoPreference(context, attrs) {
    companion object {
        const val DEFAULT_ENABLED = "1"
        const val DEFAULT_DISABLED = "0"
    }

    var enabled =
        DEFAULT_ENABLED
    var disabled =
        DEFAULT_DISABLED

    init {
        val array = context.theme.obtainStyledAttributes(attrs, R.styleable.SecureSwitchPreference, 0, 0)

        enabled = array.getString(R.styleable.SecureSwitchPreference_enabled_value) ?: DEFAULT_ENABLED
        disabled = array.getString(R.styleable.SecureSwitchPreference_disabled_value) ?: DEFAULT_DISABLED

        array.recycle()

        layoutResource = R.layout.custom_preference
    }

    override fun onAttachedToHierarchy(preferenceManager: PreferenceManager) {
        super.onAttachedToHierarchy(preferenceManager)
        summary = sharedPreferences!!.getString(key, disabled)
    }

    override fun onValueChanged(newValue: Any?, key: String) {
        super.onValueChanged(newValue, key)
        summary = newValue?.toString()
    }
}