package com.rw.tweaks.prefs.demo.base

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.edit
import androidx.preference.DialogPreference
import com.rw.tweaks.util.IDialogPreference

open class BaseDemoPreference(context: Context, attrs: AttributeSet) : DialogPreference(context, attrs), IDialogPreference {
    override var writeKey: String? = null
        get() = key

    override fun onValueChanged(newValue: Any?, key: String) {
        sharedPreferences.edit {
            putString(key, newValue?.toString())
        }
    }
}