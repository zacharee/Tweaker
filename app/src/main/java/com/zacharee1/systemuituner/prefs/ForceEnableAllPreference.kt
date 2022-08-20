package com.zacharee1.systemuituner.prefs

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceViewHolder
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.interfaces.INoPersistPreference
import com.zacharee1.systemuituner.prefs.secure.SecureSwitchPreference
import com.zacharee1.systemuituner.util.PrefManager

class ForceEnableAllPreference(context: Context, attrs: AttributeSet) : SecureSwitchPreference(context, attrs), INoPersistPreference {
    init {
        setTitle(R.string.option_advanced_force_enable_all)
        setSummary(R.string.option_advanced_force_enable_all_desc)
        setIcon(R.drawable.ic_baseline_toggle_on_24)

        disabled = "false"
        enabled = "true"
        isPersistent = true
        dangerous = true

        dialogTitle = title
        dialogMessage = summary
        dialogIcon = icon

        layoutResource = R.layout.custom_preference
        key = PrefManager.FORCE_ENABLE_ALL
        iconColor = ContextCompat.getColor(context, R.color.pref_color_4)
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        bindVH(holder)
    }

    override fun onValueChanged(newValue: Any?, key: String) {
        persistBoolean(newValue.toString().toBoolean())
    }
}