package com.zacharee1.systemuituner.views

import android.content.Context
import android.provider.Settings
import android.util.AttributeSet
import android.widget.ScrollView
import com.zacharee1.systemuituner.data.AnimationScalesData
import com.zacharee1.systemuituner.databinding.AnimationDialogBinding
import com.zacharee1.systemuituner.interfaces.IOptionDialogCallback
import com.zacharee1.systemuituner.data.SettingsType
import com.zacharee1.systemuituner.util.getSetting
import com.zacharee1.systemuituner.util.launch
import tk.zwander.seekbarpreference.SeekBarView

class AnimationScales(context: Context, attrs: AttributeSet) : ScrollView(context, attrs), IOptionDialogCallback {
    override var callback: (suspend (data: Any?) -> Boolean)? = null
    private val scaleData = AnimationScalesData()

    private val binding by lazy { AnimationDialogBinding.bind(this) }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        val initialAnimatorScale = context.getSetting(SettingsType.GLOBAL, Settings.Global.ANIMATOR_DURATION_SCALE, 1f)?.toFloatOrNull() ?: 1f
        val initialWindowScale = context.getSetting(SettingsType.GLOBAL, Settings.Global.WINDOW_ANIMATION_SCALE, 1f)?.toFloatOrNull() ?: 1f
        val initialTransitionScale = context.getSetting(SettingsType.GLOBAL, Settings.Global.TRANSITION_ANIMATION_SCALE, 1f)?.toFloatOrNull() ?: 1f

        scaleData.animatorScale = initialAnimatorScale
        scaleData.windowScale = initialWindowScale
        scaleData.transitionScale = initialTransitionScale

        binding.animatorSb.apply {
            scaledProgress = scaleData.animatorScale
            listener = object : SeekBarView.SeekBarListener {
                override fun onProgressAdded() {}
                override fun onProgressReset() {}
                override fun onProgressSubtracted() {}
                override fun onProgressChanged(newValue: Int, newScaledValue: Float) {
                    val thisRef = this

                    launch {
                        scaleData.animatorScale = newScaledValue
                        if (callback?.invoke(scaleData) == false) {
                            scaleData.animatorScale = initialAnimatorScale
                            listener = null
                            scaledProgress = scaleData.animatorScale
                            listener = thisRef
                        }
                    }
                }
            }
        }

        binding.windowSb.apply {
            scaledProgress = scaleData.windowScale
            listener = object : SeekBarView.SeekBarListener {
                override fun onProgressAdded() {}
                override fun onProgressReset() {}
                override fun onProgressSubtracted() {}
                override fun onProgressChanged(newValue: Int, newScaledValue: Float) {
                    val thisRef = this

                    launch {
                        scaleData.windowScale = newScaledValue
                        if (callback?.invoke(scaleData) == false) {
                            scaleData.windowScale = initialWindowScale
                            listener = null
                            scaledProgress = scaleData.windowScale
                            listener = thisRef
                        }
                    }
                }
            }
        }

        binding.transitionSb.apply {
            scaledProgress = scaleData.transitionScale
            listener = object : SeekBarView.SeekBarListener {
                override fun onProgressAdded() {}
                override fun onProgressReset() {}
                override fun onProgressSubtracted() {}
                override fun onProgressChanged(newValue: Int, newScaledValue: Float) {
                    val thisRef = this

                    launch {
                        scaleData.transitionScale = newScaledValue
                        if (callback?.invoke(scaleData) == false) {
                            scaleData.transitionScale = initialTransitionScale
                            listener = null
                            scaledProgress = scaleData.transitionScale
                            listener = thisRef
                        }
                    }
                }
            }
        }
    }
}