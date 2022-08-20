package com.zacharee1.systemuituner.prefs.secure.specific

import android.content.Context
import android.provider.Settings
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.data.CameraGesturesData
import com.zacharee1.systemuituner.interfaces.ISpecificPreference
import com.zacharee1.systemuituner.prefs.base.BaseDialogPreference
import com.zacharee1.systemuituner.data.SettingsType
import com.zacharee1.systemuituner.util.prefManager
import com.zacharee1.systemuituner.util.writeSetting

class CameraGesturesPreference(context: Context, attrs: AttributeSet) : BaseDialogPreference(context, attrs),
    ISpecificPreference {
    override val keys = hashMapOf(
        SettingsType.SECURE to arrayOf(
            Settings.Secure.CAMERA_DOUBLE_TAP_POWER_GESTURE_DISABLED,
            Settings.Secure.CAMERA_DOUBLE_TWIST_TO_FLIP_ENABLED,
            Settings.Secure.CAMERA_GESTURE_DISABLED
        )
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

    override fun onValueChanged(newValue: Any?, key: String) {
        val data = newValue as CameraGesturesData

        context.apply {
            prefManager.apply {
                saveOption(SettingsType.SECURE, Settings.Secure.CAMERA_DOUBLE_TWIST_TO_FLIP_ENABLED, data.doubleTwistToFlipEnabled)
                saveOption(SettingsType.SECURE, Settings.Secure.CAMERA_GESTURE_DISABLED, data.cameraGestureDisabled)
                saveOption(SettingsType.SECURE, Settings.Secure.CAMERA_DOUBLE_TAP_POWER_GESTURE_DISABLED, data.doubleTapPowerDisabled)
            }

            writeSetting(SettingsType.SECURE, Settings.Secure.CAMERA_DOUBLE_TWIST_TO_FLIP_ENABLED, data.doubleTwistToFlipEnabled)
            writeSetting(SettingsType.SECURE, Settings.Secure.CAMERA_GESTURE_DISABLED, data.cameraGestureDisabled)
            writeSetting(SettingsType.SECURE, Settings.Secure.CAMERA_DOUBLE_TAP_POWER_GESTURE_DISABLED, data.doubleTapPowerDisabled)
        }
    }
}