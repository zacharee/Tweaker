package com.zacharee1.systemuituner.views

import android.content.Context
import android.provider.Settings
import android.util.AttributeSet
import android.widget.ScrollView
import com.zacharee1.systemuituner.data.CameraGesturesData
import com.zacharee1.systemuituner.interfaces.IOptionDialogCallback
import com.zacharee1.systemuituner.util.SettingsType
import com.zacharee1.systemuituner.util.getSetting
import com.zacharee1.systemuituner.util.prefManager
import com.zacharee1.systemuituner.util.writeSecure
import kotlinx.android.synthetic.main.camera_gestures.view.*

class CameraGestures(context: Context, attrs: AttributeSet) : ScrollView(context, attrs), IOptionDialogCallback {
    override var callback: ((data: Any?) -> Unit)? = null
    private val cameraData = CameraGesturesData()

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        cameraData.cameraGestureDisabled = context.getSetting(SettingsType.SECURE, Settings.Secure.CAMERA_DOUBLE_TAP_POWER_GESTURE_DISABLED, 1)!!.toInt()
        cameraData.doubleTapPowerDisabled = context.getSetting(SettingsType.SECURE, Settings.Secure.CAMERA_DOUBLE_TAP_POWER_GESTURE_DISABLED, 1)!!.toInt()
        cameraData.doubleTwistToFlipEnabled = context.getSetting(SettingsType.SECURE, Settings.Secure.CAMERA_DOUBLE_TWIST_TO_FLIP_ENABLED, 0)!!.toInt()

        camera_gesture.apply {
            isChecked = cameraData.cameraGestureDisabled == 0
            setOnCheckedChangeListener { _, isChecked ->
                cameraData.cameraGestureDisabled = if (isChecked) 0 else 1
                callback?.invoke(cameraData)
            }
        }

        camera_power_button.apply {
            isChecked = cameraData.doubleTapPowerDisabled == 0
            setOnCheckedChangeListener { _, isChecked ->
                cameraData.doubleTapPowerDisabled = if (isChecked) 0 else 1
                callback?.invoke(cameraData)
            }
        }

        camera_twist.apply {
            isChecked = cameraData.doubleTwistToFlipEnabled == 1
            setOnCheckedChangeListener { _, isChecked ->
                cameraData.doubleTwistToFlipEnabled = if (isChecked) 1 else 0
                callback?.invoke(cameraData)
            }
        }
    }
}