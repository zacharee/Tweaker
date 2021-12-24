package com.zacharee1.systemuituner.prefs.secure.specific

import android.app.UiModeManager
import android.content.Context
import android.content.res.TypedArray
import android.provider.Settings
import android.util.AttributeSet
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.prefs.secure.SecureSwitchPreference
import com.zacharee1.systemuituner.util.SettingsType

class DarkModePreference(context: Context, attrs: AttributeSet) : SecureSwitchPreference(context, attrs) {
    private val uim = context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager

    init {
        key = Settings.Secure.UI_NIGHT_MODE
        type = SettingsType.SECURE

        enabled = UiModeManager.MODE_NIGHT_YES.toString()
        disabled = UiModeManager.MODE_NIGHT_NO.toString()

        setTitle(R.string.feature_dark_mode)
        setSummary(R.string.feature_dark_mode_desc)
        setIcon(R.drawable.light_dark)

        iconColor = context.getColor(R.color.pref_color_5)

        dialogTitle = title
        dialogMessage = summary
    }

    override fun onGetDefaultValue(a: TypedArray, index: Int): Any {
        return UiModeManager.MODE_NIGHT_NO.toString()
    }

    override fun onValueChanged(newValue: Any?, key: String) {
        super.onValueChanged(newValue, key)

        uim.nightMode = newValue.toString().toInt()
    }
}