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
import com.zacharee1.systemuituner.util.launch

class CameraGestures(context: Context, attrs: AttributeSet) : ScrollView(context, attrs), IOptionDialogCallback {
    override var callback: (suspend (data: Any?) -> Boolean)? = null
    private val cameraData = CameraGesturesData()

    private val binding by lazy { CameraGesturesBinding.bind(this) }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        cameraData.cameraGestureDisabled = context.getSetting(SettingsType.SECURE, Settings.Secure.CAMERA_DOUBLE_TAP_POWER_GESTURE_DISABLED, 1)?.toIntOrNull() ?: 1
        cameraData.doubleTapPowerDisabled = context.getSetting(SettingsType.SECURE, Settings.Secure.CAMERA_DOUBLE_TAP_POWER_GESTURE_DISABLED, 1)?.toIntOrNull() ?: 1
        cameraData.doubleTwistToFlipEnabled = context.getSetting(SettingsType.SECURE, Settings.Secure.CAMERA_DOUBLE_TWIST_TO_FLIP_ENABLED, 0)?.toIntOrNull() ?: 1

        binding.cameraGesture.apply {
            isChecked = cameraData.cameraGestureDisabled == 0
            setOnCheckedChangeListener { _, isChecked ->
                val newValue = if (isChecked) 0 else 1

                if (newValue != cameraData.cameraGestureDisabled) {
                    cameraData.cameraGestureDisabled = if (isChecked) 0 else 1

                    launch {
                        if (callback?.invoke(cameraData) == false) {
                            cameraData.cameraGestureDisabled = if (isChecked) 1 else 0
                            this@apply.isChecked = !isChecked
                        }
                    }
                }
            }
        }

        binding.cameraPowerButton.apply {
            isChecked = cameraData.doubleTapPowerDisabled == 0
            setOnCheckedChangeListener { _, isChecked ->
                val newValue = if (isChecked) 0 else 1

                if (newValue != cameraData.doubleTapPowerDisabled) {
                    cameraData.doubleTapPowerDisabled = if (isChecked) 0 else 1

                    launch {
                        if (callback?.invoke(cameraData) == false) {
                            cameraData.doubleTapPowerDisabled = if (isChecked) 1 else 0
                            this@apply.isChecked = !isChecked
                        }
                    }
                }
            }
        }

        binding.cameraTwist.apply {
            isChecked = cameraData.doubleTwistToFlipEnabled == 1
            setOnCheckedChangeListener { _, isChecked ->
                val newValue = if (isChecked) 0 else 1

                if (newValue != cameraData.doubleTwistToFlipEnabled) {
                    cameraData.doubleTwistToFlipEnabled = if (isChecked) 0 else 1

                    launch {
                        if (callback?.invoke(cameraData) == false) {
                            cameraData.doubleTwistToFlipEnabled = if (isChecked) 1 else 0
                            this@apply.isChecked = !isChecked
                        }
                    }
                }
            }
        }
    }
}