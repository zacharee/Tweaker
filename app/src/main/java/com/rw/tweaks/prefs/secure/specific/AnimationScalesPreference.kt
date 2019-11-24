package com.rw.tweaks.prefs.secure.specific

import android.content.Context
import android.util.AttributeSet
import androidx.preference.DialogPreference
import com.rw.tweaks.R
import com.rw.tweaks.util.ISecurePreference
import com.rw.tweaks.util.SecurePreference

class AnimationScalesPreference(context: Context, attrs: AttributeSet) : DialogPreference(context, attrs), ISecurePreference by SecurePreference(context) {
    init {
        key = "anim"

        setTitle(R.string.feature_custom_animation_scales)
        setSummary(R.string.feature_custom_animation_scales_desc)

        dialogTitle = title
        dialogMessage = summary
        setIcon(R.drawable.animation)

        init(this)
    }
}