package com.zacharee1.systemuituner.prefs

import android.content.Context
import android.util.AttributeSet
import androidx.preference.PreferenceViewHolder
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.interfaces.INoPersistPreference
import com.zacharee1.systemuituner.prefs.base.BaseDialogPreference

class WriteSettingPreference(context: Context, attrs: AttributeSet) : BaseDialogPreference(context, attrs), INoPersistPreference {
    init {
        layoutResource = R.layout.custom_preference
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        bindVH(holder)
    }
}