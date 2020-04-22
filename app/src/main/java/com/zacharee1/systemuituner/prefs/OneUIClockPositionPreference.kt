package com.zacharee1.systemuituner.prefs

import android.content.Context
import android.util.AttributeSet
import androidx.preference.DialogPreference
import androidx.preference.PreferenceViewHolder
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.interfaces.*
import com.zacharee1.systemuituner.util.prefManager
import com.zacharee1.systemuituner.util.writeSecure

class OneUIClockPositionPreference(context: Context, attrs: AttributeSet) : DialogPreference(context, attrs), IColorPreference by ColorPreference(
    context,
    attrs
), INoPersistPreference, IVerifierPreference by VerifierPreference(context, attrs), IDialogPreference {
    override var writeKey: String?
        get() = key
        set(value) {
            key = value
        }

    init {
        layoutResource = R.layout.custom_preference
        init(this)
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        bindVH(holder)
    }

    override fun onValueChanged(newValue: Any?, key: String) {
        val string = newValue?.toString()

        context.prefManager.blacklistedItems = HashSet(string?.split(",") ?: listOf())
        context.writeSecure("icon_blacklist", string)
    }
}