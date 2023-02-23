package com.zacharee1.systemuituner.prefs.base

import android.content.Context
import android.util.AttributeSet
import androidx.preference.DialogPreference
import androidx.preference.PreferenceManager
import androidx.preference.PreferenceViewHolder
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.interfaces.*

open class BaseDialogPreference(context: Context, attrs: AttributeSet) : DialogPreference(context, attrs), IColorPreference by ColorPreference(context, attrs),
        IVerifierPreference by VerifierPreference(context, attrs), IDialogPreference {

    init {
        layoutResource = R.layout.custom_preference
    }

    override fun onAttachedToHierarchy(preferenceManager: PreferenceManager) {
        super.onAttachedToHierarchy(preferenceManager)

        dialogMessage = summary
        dialogTitle = title
        dialogIcon = icon
        initVerify(this)
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        bindVH(holder)
    }

    override fun onValueChanged(newValue: Any?, key: String): Boolean {
        return true
    }
}