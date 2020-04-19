package com.zacharee1.systemuituner.prefs.secure.specific

import android.content.Context
import android.provider.Settings
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.prefs.secure.base.BaseSecurePreference
import com.zacharee1.systemuituner.util.ISpecificPreference
import com.zacharee1.systemuituner.util.SettingsType

class CameraGesturesPreference(context: Context, attrs: AttributeSet) : BaseSecurePreference(context, attrs), ISpecificPreference {
    override var type: SettingsType = SettingsType.SECURE
    override val keys: Array<String> = arrayOf(
        Settings.Secure.CAMERA_DOUBLE_TAP_POWER_GESTURE_DISABLED,
        Settings.Secure.CAMERA_DOUBLE_TWIST_TO_FLIP_ENABLED,
        Settings.Secure.CAMERA_GESTURE_DISABLED
    )

    init {
        key = "camera_gestures"

        setTitle(R.string.feature_camera_gestures)
        setSummary(R.string.feature_camera_gestures_desc)

        dialogTitle = title
        dialogMessage = summary
        setIcon(R.drawable.ic_baseline_camera_24)
        iconColor = ContextCompat.getColor(context, R.color.pref_color_6)
    }
}