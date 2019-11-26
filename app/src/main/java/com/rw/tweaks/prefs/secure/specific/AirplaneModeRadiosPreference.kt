package com.rw.tweaks.prefs.secure.specific

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import androidx.preference.DialogPreference
import com.rw.tweaks.R
import com.rw.tweaks.util.ISecurePreference
import com.rw.tweaks.util.SecurePreference

class AirplaneModeRadiosPreference(context: Context, attrs: AttributeSet) : DialogPreference(context, attrs), ISecurePreference by SecurePreference(context) {
    init {
        key = "airplane_mode_radios"

        setTitle(R.string.special_sub_airplane_mode)
        setSummary(R.string.special_sub_airplane_mode_desc)

        dialogTitle = title
        dialogMessage = summary
        setIcon(R.drawable.ic_baseline_airplanemode_active_24)
        iconColor = ContextCompat.getColor(context, R.color.pref_color_4)

        init(this)
    }
}