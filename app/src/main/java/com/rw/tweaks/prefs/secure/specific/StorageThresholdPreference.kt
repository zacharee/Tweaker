package com.rw.tweaks.prefs.secure.specific

import android.content.Context
import android.util.AttributeSet
import androidx.preference.DialogPreference
import com.rw.tweaks.R
import com.rw.tweaks.util.ISecurePreference
import com.rw.tweaks.util.SecurePreference

class StorageThresholdPreference(context: Context, attrs: AttributeSet) : DialogPreference(context, attrs), ISecurePreference by SecurePreference() {
    init {
        key = "storage"

        setTitle(R.string.feature_insufficient_storage_warning)
        setSummary(R.string.feature_insufficient_storage_warning_desc)

        dialogTitle = title
        dialogMessage = summary

        init(this)
    }
}