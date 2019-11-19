package com.rw.tweaks.prefs.secure.specific

import android.content.Context
import android.util.AttributeSet
import androidx.preference.DialogPreference
import com.rw.tweaks.R
import com.rw.tweaks.util.ISecurePreference
import com.rw.tweaks.util.SecurePreference

class KeepDeviceOnPluggedPreference(context: Context, attrs: AttributeSet) : DialogPreference(context, attrs), ISecurePreference by SecurePreference() {
    init {
        key = "keep_device_on"

        setTitle(R.string.feature_keep_screen_on)
        setSummary(R.string.feature_keep_screen_on_desc)

        dialogTitle = title
        dialogMessage = summary

        init(this)
    }
}