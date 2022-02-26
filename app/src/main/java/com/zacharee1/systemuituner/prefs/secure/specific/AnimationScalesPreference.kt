package com.zacharee1.systemuituner.prefs.secure.specific

import android.content.Context
import android.provider.Settings
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.data.AnimationScalesData
import com.zacharee1.systemuituner.interfaces.ISpecificPreference
import com.zacharee1.systemuituner.prefs.base.BaseDialogPreference
import com.zacharee1.systemuituner.util.SettingsType
import com.zacharee1.systemuituner.util.prefManager
import com.zacharee1.systemuituner.util.writeGlobal

class AnimationScalesPreference(context: Context, attrs: AttributeSet) : BaseDialogPreference(context, attrs),
    ISpecificPreference {
    override val keys = hashMapOf(
        SettingsType.GLOBAL to arrayOf(
            Settings.Global.ANIMATOR_DURATION_SCALE,
            Settings.Global.TRANSITION_ANIMATION_SCALE,
            Settings.Global.WINDOW_ANIMATION_SCALE
        )
    )

    init {
        key = "anim"

        setTitle(R.string.feature_custom_animation_scales)
        setSummary(R.string.feature_custom_animation_scales_desc)

        dialogTitle = title
        dialogMessage = summary
        setIcon(R.drawable.animation)
        iconColor = ContextCompat.getColor(context, R.color.pref_color_7)
    }

    override fun onValueChanged(newValue: Any?, key: String) {
        val data = newValue as AnimationScalesData

        context.apply {
            prefManager.saveOption(SettingsType.GLOBAL, Settings.Global.ANIMATOR_DURATION_SCALE, data.animatorScale)
            writeGlobal(Settings.Global.ANIMATOR_DURATION_SCALE, data.animatorScale)
            prefManager.saveOption(SettingsType.GLOBAL, Settings.Global.WINDOW_ANIMATION_SCALE, data.windowScale)
            writeGlobal(Settings.Global.WINDOW_ANIMATION_SCALE, data.windowScale)
            prefManager.saveOption(SettingsType.GLOBAL, Settings.Global.TRANSITION_ANIMATION_SCALE, data.transitionScale)
            writeGlobal(Settings.Global.TRANSITION_ANIMATION_SCALE, data.transitionScale)
        }
    }
}