package com.rw.tweaks.prefs.secure

import android.content.Context
import android.util.AttributeSet
import com.rw.tweaks.R
import com.rw.tweaks.prefs.secure.base.BaseSecurePreference
import com.rw.tweaks.util.prefManager
import com.rw.tweaks.util.writeSetting

class SecureSwitchPreference(context: Context, attrs: AttributeSet) : BaseSecurePreference(context, attrs) {
    companion object {
        const val DEFAULT_ENABLED = "1"
        const val DEFAULT_DISABLED = "0"
    }

    var enabled = DEFAULT_ENABLED
    var disabled = DEFAULT_DISABLED

    init {
        isPersistent = false
        val array = context.theme.obtainStyledAttributes(attrs, R.styleable.SecureSwitchPreference, 0, 0)

        enabled = array.getString(R.styleable.SecureSwitchPreference_enabled_value) ?: DEFAULT_ENABLED
        disabled = array.getString(R.styleable.SecureSwitchPreference_disabled_value) ?: DEFAULT_DISABLED

        array.recycle()
    }

    override fun onValueChanged(newValue: Any?, key: String?) {
        context.prefManager.putString(key!!, newValue?.toString())
        context.writeSetting(type, writeKey, newValue)
    }
}