package com.zacharee1.systemuituner.views

import android.content.Context
import android.provider.Settings
import android.util.AttributeSet
import android.widget.ScrollView
import com.zacharee1.systemuituner.data.CameraGesturesData
import com.zacharee1.systemuituner.databinding.CameraGesturesBinding
import com.zacharee1.systemuituner.interfaces.IOptionDialogCallback
import com.zacharee1.systemuituner.data.SettingsType
import com.zacharee1.systemuituner.util.getSetting

class CameraGestures(context: Context, attrs: AttributeSet) : ScrollView(context, attrs), IOptionDialogCallback {
    override var callback: ((data: Any?) -> Unit)? = null
    private val cameraData = CameraGesturesData()

    private val binding by lazy { CameraGesturesBinding.bind(this) }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        cameraData.cameraGestureDisabled = context.getSetting(SettingsType.SECURE, Settings.Secure.CAMERA_DOUBLE_TAP_POWER_GESTURE_DISABLED, 1)!!.toInt()
        cameraData.doubleTapPowerDisabled = context.getSetting(SettingsType.SECURE, Settings.Secure.CAMERA_DOUBLE_TAP_POWER_GESTURE_DISABLED, 1)!!.toInt()
        cameraData.doubleTwistToFlipEnabled = context.getSetting(SettingsType.SECURE, Settings.Secure.CAMERA_DOUBLE_TWIST_TO_FLIP_ENABLED, 0)!!.toInt()

        binding.cameraGesture.apply {
            isChecked = cameraData.cameraGestureDisabled == 0
            setOnCheckedChangeListener { _, isChecked ->
                cameraData.cameraGestureDisabled = if (isChecked) 0 else 1
                callback?.invoke(cameraData)
            }
        }

        binding.cameraPowerButton.apply {
            isChecked = cameraData.doubleTapPowerDisabled == 0
            setOnCheckedChangeListener { _, isChecked ->
                cameraData.doubleTapPowerDisabled = if (isChecked) 0 else 1
                callback?.invoke(cameraData)
            }
        }

        binding.cameraTwist.apply {
            isChecked = cameraData.doubleTwistToFlipEnabled == 1
            setOnCheckedChangeListener { _, isChecked ->
                cameraData.doubleTwistToFlipEnabled = if (isChecked) 1 else 0
                callback?.invoke(cameraData)
            }
        }
    }
}