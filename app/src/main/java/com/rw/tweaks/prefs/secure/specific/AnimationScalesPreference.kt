package com.rw.tweaks.prefs.secure.specific

import android.content.Context
import android.util.AttributeSet
import androidx.preference.DialogPreference
import com.rw.tweaks.R
import com.rw.tweaks.util.ISecurePreference
import com.rw.tweaks.util.SettingsType

class AnimationScalesPreference(context: Context, attrs: AttributeSet) : DialogPreference(context, attrs), ISecurePreference {
    override var type: SettingsType
        get() = SettingsType.UNDEFINED
        set(value) {}
    override var writeKey: String?
        get() = null
        set(value) {}
    override var dangerous = false

    init {
        key = "anim"

        setTitle(R.string.feature_custom_animation_scales)
        setSummary(R.string.feature_custom_animation_scales_desc)
        dialogTitle = title
        dialogMessage = summary
    }

    override fun onValueChanged(newValue: Any?, key: String?) {}
}