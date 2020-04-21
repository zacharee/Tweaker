package com.zacharee1.systemuituner.prefs

import android.content.Context
import android.util.AttributeSet
import androidx.preference.DialogPreference
import androidx.preference.PreferenceViewHolder
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.interfaces.ColorPreference
import com.zacharee1.systemuituner.interfaces.IColorPreference
import com.zacharee1.systemuituner.interfaces.INoPersistPreference

class WriteSettingPreference(context: Context, attrs: AttributeSet) : DialogPreference(context, attrs), IColorPreference by ColorPreference(
    context,
    attrs
), INoPersistPreference {
    init {
        layoutResource = R.layout.custom_preference
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        bindVH(holder)
    }
}