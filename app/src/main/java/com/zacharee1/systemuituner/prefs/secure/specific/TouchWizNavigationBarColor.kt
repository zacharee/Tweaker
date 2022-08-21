package com.zacharee1.systemuituner.prefs.secure.specific

import android.content.Context
import android.util.AttributeSet
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.interfaces.ISpecificPreference
import com.zacharee1.systemuituner.prefs.secure.base.BaseSecurePreference
import com.zacharee1.systemuituner.data.SettingsType
import com.zacharee1.systemuituner.util.SettingsInfo
import com.zacharee1.systemuituner.util.verifiers.ShowForTouchWiz
import com.zacharee1.systemuituner.util.writeSettingsBulk

class TouchWizNavigationBarColor(context: Context, attrs: AttributeSet) : BaseSecurePreference(context, attrs), ISpecificPreference {
    override val keys
        get() = hashMapOf(
            SettingsType.GLOBAL to arrayOf(key, "navigationbar_current_color")
        )
    override var type = SettingsType.UNDEFINED

    init {
        key = "navigationbar_color"
        setTitle(R.string.option_touchwiz_navbar_color)
        setSummary(R.string.option_touchwiz_navbar_color_desc)

        dialogTitle = title
        dialogMessage = summary
        iconColor = context.resources.getColor(R.color.pref_color_5, context.theme)
        setIcon(R.drawable.ic_baseline_color_lens_24)

        visibilityVerifier = ShowForTouchWiz(context)
    }

    override fun onValueChanged(newValue: Any?, key: String) {
        context.writeSettingsBulk(
            SettingsInfo(SettingsType.GLOBAL, "navigationbar_color", newValue),
            SettingsInfo(SettingsType.GLOBAL, "navigationbar_current_color", newValue),
            saveOption = true
        )
    }
}