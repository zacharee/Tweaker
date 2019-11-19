package com.rw.tweaks.prefs.secure.specific

import android.content.Context
import android.util.AttributeSet
import androidx.preference.DialogPreference
import com.rw.tweaks.R
import com.rw.tweaks.util.ISecurePreference
import com.rw.tweaks.util.SecurePreference

class SMSLimitsPreference(context: Context, attrs: AttributeSet) : DialogPreference(context, attrs), ISecurePreference by SecurePreference(context) {
    init {
        key = "sms_limits"

        setTitle(R.string.feature_sms_limit)
        setSummary(R.string.feature_sms_limit_desc)

        dialogTitle = title
        dialogMessage = summary

        init(this)
    }
}