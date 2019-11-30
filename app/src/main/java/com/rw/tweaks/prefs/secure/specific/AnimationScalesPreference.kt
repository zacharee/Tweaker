package com.rw.tweaks.prefs.secure.specific

import android.content.Context
import android.provider.Settings
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import androidx.preference.DialogPreference
import com.rw.tweaks.R
import com.rw.tweaks.util.ISecurePreference
import com.rw.tweaks.util.ISpecificPreference
import com.rw.tweaks.util.SecurePreference
import com.rw.tweaks.util.SettingsType

class AnimationScalesPreference(context: Context, attrs: AttributeSet) : DialogPreference(context, attrs), ISecurePreference by SecurePreference(context), ISpecificPreference {
    override var type: SettingsType = SettingsType.GLOBAL
    override val keys: Array<String> = arrayOf(Settings.Global.ANIMATOR_DURATION_SCALE, Settings.Global.TRANSITION_ANIMATION_SCALE, Settings.Global.WINDOW_ANIMATION_SCALE)

    init {
        key = "anim"

        setTitle(R.string.feature_custom_animation_scales)
        setSummary(R.string.feature_custom_animation_scales_desc)

        dialogTitle = title
        dialogMessage = summary
        setIcon(R.drawable.animation)
        iconColor = ContextCompat.getColor(context, R.color.pref_color_7)

        init(this)
    }
}