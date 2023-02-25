package com.zacharee1.systemuituner.prefs.secure.specific

import android.content.Context
import android.provider.Settings
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.interfaces.ISpecificPreference
import com.zacharee1.systemuituner.prefs.base.BaseDialogPreference
import com.zacharee1.systemuituner.data.SettingsType
import com.zacharee1.systemuituner.util.getSetting
import com.zacharee1.systemuituner.util.writeSetting

class TetheringPreference(context: Context, attrs: AttributeSet) : BaseDialogPreference(context, attrs),
    ISpecificPreference {
    override val keys = hashMapOf(
        SettingsType.GLOBAL to arrayOf(
            Settings.Global.TETHER_DUN_REQUIRED,
            Settings.Global.TETHER_SUPPORTED
        )
    )

    val bothFixed: Boolean
        get() = context.getSetting(SettingsType.GLOBAL, Settings.Global.TETHER_DUN_REQUIRED) == "0"
                && context.getSetting(SettingsType.GLOBAL, Settings.Global.TETHER_SUPPORTED) == "true"

    init {
        key = "tethering_fix"

        setTitle(R.string.feature_fix_tethering)
        setSummary(R.string.feature_fix_tethering_desc)

        dialogTitle = title
        dialogMessage = summary
        setIcon(R.drawable.link)
        iconColor = ContextCompat.getColor(context, R.color.pref_color_1)
    }

    override suspend fun onValueChanged(newValue: Any?, key: String): Boolean {
        val enabled = newValue.toString().toBoolean()

        return context.writeSetting(SettingsType.GLOBAL, Settings.Global.TETHER_DUN_REQUIRED, if (enabled) 0 else 1, saveOption = true) &&
                context.writeSetting(SettingsType.GLOBAL, Settings.Global.TETHER_SUPPORTED, enabled, saveOption = true)
    }
}