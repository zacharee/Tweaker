package com.rw.tweaks.prefs

import android.content.Context
import android.util.AttributeSet
import androidx.preference.DialogPreference
import com.rw.tweaks.R
import com.rw.tweaks.util.ISecurePreference
import com.rw.tweaks.util.SettingsType

class StorageThresholdPreference(context: Context, attrs: AttributeSet) : DialogPreference(context, attrs), ISecurePreference {
    override var type: SettingsType
        get() = SettingsType.UNDEFINED
        set(value) {}
    override var writeKey: String?
        get() = null
        set(value) {}

    init {
        key = "storage"

        setTitle(R.string.feature_insufficient_storage_warning)
        setSummary(R.string.feature_insufficient_storage_warning_desc)

        dialogTitle = title
        dialogMessage = summary
    }

    override fun onValueChanged(newValue: Any?, key: String?) {}
}