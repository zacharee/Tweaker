package com.zacharee1.systemuituner.prefs.secure.specific

import android.content.Context
import android.provider.Settings
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.prefs.secure.base.BaseSecurePreference
import com.zacharee1.systemuituner.interfaces.ISpecificPreference
import com.zacharee1.systemuituner.prefs.base.BaseDialogPreference
import com.zacharee1.systemuituner.util.SettingsType
import com.zacharee1.systemuituner.util.prefManager
import com.zacharee1.systemuituner.util.writeGlobal

class TetheringPreference(context: Context, attrs: AttributeSet) : BaseDialogPreference(context, attrs),
    ISpecificPreference {
    override val keys = hashMapOf(
        SettingsType.GLOBAL to arrayOf(
            Settings.Global.TETHER_DUN_REQUIRED,
            Settings.Global.TETHER_SUPPORTED
        )
    )

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
    }

    override fun onValueChanged(newValue: Any?, key: String) {
        val enabled = newValue.toString().toBoolean()

        context.prefManager.apply {
            saveOption(SettingsType.GLOBAL, Settings.Global.TETHER_DUN_REQUIRED, if (enabled) 0 else 1)
            saveOption(SettingsType.GLOBAL, Settings.Global.TETHER_SUPPORTED, enabled)
        }
        context.writeGlobal(Settings.Global.TETHER_DUN_REQUIRED, if (enabled) 0 else 1)
        context.writeGlobal(Settings.Global.TETHER_SUPPORTED, enabled)
    }
}