package com.zacharee1.systemuituner.prefs.secure.base

import android.content.Context
import android.util.AttributeSet
import androidx.preference.PreferenceManager
import com.zacharee1.systemuituner.interfaces.*
import com.zacharee1.systemuituner.prefs.base.BaseDialogPreference
import com.zacharee1.systemuituner.util.*

open class BaseSecurePreference(context: Context, attrs: AttributeSet) : BaseDialogPreference(context, attrs), ISecurePreference by SecurePreference(
    context,
    attrs
) {
    override var writeKey: String = ""
        get() = field.ifBlank { key }

    override fun onAttachedToHierarchy(preferenceManager: PreferenceManager) {
        super.onAttachedToHierarchy(preferenceManager)
        initSecure(this)
    }

    override fun onValueChanged(newValue: Any?, key: String): Boolean {
        return context.writeSetting(type, writeKey, newValue, dangerous, true)
    }
}