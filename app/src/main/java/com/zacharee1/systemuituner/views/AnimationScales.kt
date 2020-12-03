package com.zacharee1.systemuituner.views

import android.content.Context
import android.provider.Settings
import android.util.AttributeSet
import android.widget.ScrollView
import com.zacharee1.systemuituner.data.AnimationScalesData
import com.zacharee1.systemuituner.interfaces.IOptionDialogCallback
import com.zacharee1.systemuituner.util.SettingsType
import com.zacharee1.systemuituner.util.getSetting
import com.zacharee1.systemuituner.util.toFloatOrDefault
import kotlinx.android.synthetic.main.animation_dialog.view.*
import tk.zwander.seekbarpreference.SeekBarView

class AnimationScales(context: Context, attrs: AttributeSet) : ScrollView(context, attrs), IOptionDialogCallback {
    override var callback: ((data: Any?) -> Unit)? = null
    private val scaleData = AnimationScalesData()

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        scaleData.animatorScale = context.getSetting(SettingsType.GLOBAL, Settings.Global.ANIMATOR_DURATION_SCALE, 1f)!!.toFloatOrDefault(1f)
        scaleData.windowScale = context.getSetting(SettingsType.GLOBAL, Settings.Global.WINDOW_ANIMATION_SCALE, 1f)!!.toFloatOrDefault(1f)
        scaleData.transitionScale = context.getSetting(SettingsType.GLOBAL, Settings.Global.TRANSITION_ANIMATION_SCALE, 1f)!!.toFloatOrDefault(1f)

        animator_sb.apply {
            scaledProgress = scaleData.animatorScale
            listener = object : SeekBarView.SeekBarListener {
                override fun onProgressAdded() {}
                override fun onProgressReset() {}
                override fun onProgressSubtracted() {}
                override fun onProgressChanged(newValue: Int, newScaledValue: Float) {
                    scaleData.animatorScale = newScaledValue
                    callback?.invoke(scaleData)
                }
            }
        }

        window_sb.apply {
            scaledProgress = scaleData.windowScale
            listener = object : SeekBarView.SeekBarListener {
                override fun onProgressAdded() {}
                override fun onProgressReset() {}
                override fun onProgressSubtracted() {}
                override fun onProgressChanged(newValue: Int, newScaledValue: Float) {
                    scaleData.windowScale = newScaledValue
                    callback?.invoke(scaleData)
                }
            }
        }

        transition_sb.apply {
            scaledProgress = scaleData.transitionScale
            listener = object : SeekBarView.SeekBarListener {
                override fun onProgressAdded() {}
                override fun onProgressReset() {}
                override fun onProgressSubtracted() {}
                override fun onProgressChanged(newValue: Int, newScaledValue: Float) {
                    scaleData.transitionScale = newScaledValue
                    callback?.invoke(scaleData)
                }
            }
        }
    }
}