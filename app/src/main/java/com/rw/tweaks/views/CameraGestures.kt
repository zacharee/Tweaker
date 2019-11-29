package com.rw.tweaks.views

import android.content.Context
import android.provider.Settings
import android.util.AttributeSet
import android.widget.ScrollView
import com.rw.tweaks.util.prefManager
import com.rw.tweaks.util.writeSecure
import kotlinx.android.synthetic.main.camera_gestures.view.*

class CameraGestures(context: Context, attrs: AttributeSet) : ScrollView(context, attrs) {
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        camera_gesture.apply {
            isChecked = Settings.Secure.getInt(context.contentResolver, Settings.Secure.CAMERA_DOUBLE_TAP_POWER_GESTURE_DISABLED, 1) == 0
            setOnCheckedChangeListener { _, isChecked ->
                context.prefManager.putInt(Settings.Secure.CAMERA_DOUBLE_TAP_POWER_GESTURE_DISABLED, if (isChecked) 0 else 1)
                context.writeSecure(Settings.Secure.CAMERA_DOUBLE_TAP_POWER_GESTURE_DISABLED, if (isChecked) 0 else 1)
            }
        }

        camera_power_button.apply {
            isChecked = Settings.Secure.getInt(context.contentResolver, Settings.Secure.CAMERA_GESTURE_DISABLED, 1) == 0
            setOnCheckedChangeListener { _, isChecked ->
                context.prefManager.putInt(Settings.Secure.CAMERA_GESTURE_DISABLED, if (isChecked) 0 else 1)
                context.writeSecure(Settings.Secure.CAMERA_GESTURE_DISABLED, if (isChecked) 0 else 1)
            }
        }

        camera_twist.apply {
            isChecked = Settings.Secure.getInt(context.contentResolver, Settings.Secure.CAMERA_DOUBLE_TWIST_TO_FLIP_ENABLED, 0) == 1
            setOnCheckedChangeListener { _, isChecked ->
                context.prefManager.putInt(Settings.Secure.CAMERA_DOUBLE_TWIST_TO_FLIP_ENABLED, if (isChecked) 1 else 0)
                context.writeSecure(Settings.Secure.CAMERA_DOUBLE_TWIST_TO_FLIP_ENABLED, if (isChecked) 1 else 0)
            }
        }
    }
}