package com.zacharee1.systemuituner.prefs

import android.content.Context
import android.util.AttributeSet
import androidx.preference.PreferenceViewHolder
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.data.SettingsType
import com.zacharee1.systemuituner.interfaces.INoPersistPreference
import com.zacharee1.systemuituner.prefs.base.BaseDialogPreference
import com.zacharee1.systemuituner.util.prefManager
import com.zacharee1.systemuituner.util.writeSetting

class OneUIClockPositionPreference(context: Context, attrs: AttributeSet) : BaseDialogPreference(context, attrs), INoPersistPreference {
    init {
        layoutResource = R.layout.custom_preference
        initVerify(this)
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        bindVH(holder)
    }

    override fun onValueChanged(newValue: Any?, key: String): Boolean {
        val string = newValue?.toString()

        context.prefManager.blacklistedItems = HashSet(string?.split(",") ?: listOf())
        return context.writeSetting(SettingsType.SECURE, "icon_blacklist", string)
    }
}