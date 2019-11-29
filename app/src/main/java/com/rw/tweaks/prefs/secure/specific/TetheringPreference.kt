package com.rw.tweaks.prefs.secure.specific

import android.content.Context
import android.provider.Settings
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import androidx.preference.DialogPreference
import com.rw.tweaks.R
import com.rw.tweaks.util.ISecurePreference
import com.rw.tweaks.util.SecurePreference
import com.rw.tweaks.util.prefManager
import com.rw.tweaks.util.writeGlobal

class TetheringPreference(context: Context, attrs: AttributeSet) : DialogPreference(context, attrs), ISecurePreference by SecurePreference(context) {
    val bothFixed: Boolean
        get() = Settings.Global.getInt(context.contentResolver, Settings.Global.TETHER_DUN_REQUIRED, 1) == 0
                && Settings.Global.getString(context.contentResolver, Settings.Global.TETHER_SUPPORTED) == "true"

    init {
        key = "tethering_fix"

        setTitle(R.string.feature_fix_tethering)
        setSummary(R.string.feature_fix_tethering_desc)

        dialogTitle = title
        dialogMessage = summary
        setIcon(R.drawable.link)
        iconColor = ContextCompat.getColor(context, R.color.pref_color_1)

        init(this)
    }

    override fun onValueChanged(newValue: Any?, key: String?) {
        val enabled = newValue.toString().toBoolean()

        context.prefManager.putInt(Settings.Global.TETHER_DUN_REQUIRED, if (enabled) 0 else 1)
        context.writeGlobal(Settings.Global.TETHER_DUN_REQUIRED, if (enabled) 0 else 1)
        context.prefManager.putString(Settings.Global.TETHER_SUPPORTED, if (enabled) "true" else "false")
        context.writeGlobal(Settings.Global.TETHER_SUPPORTED, if (enabled) "true" else "false")
    }
}