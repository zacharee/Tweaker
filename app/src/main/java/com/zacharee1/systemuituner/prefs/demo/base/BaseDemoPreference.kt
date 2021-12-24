package com.zacharee1.systemuituner.prefs.demo.base

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.edit
import androidx.preference.DialogPreference
import com.zacharee1.systemuituner.interfaces.IDialogPreference

open class BaseDemoPreference(context: Context, attrs: AttributeSet) : DialogPreference(context, attrs),
    IDialogPreference {
    override fun onValueChanged(newValue: Any?, key: String) {
        sharedPreferences!!.edit {
            putString(key, newValue?.toString())
        }
    }
}