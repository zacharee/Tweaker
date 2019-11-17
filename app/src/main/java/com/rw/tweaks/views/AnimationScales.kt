package com.rw.tweaks.views

import android.content.Context
import android.provider.Settings
import android.util.AttributeSet
import android.widget.ScrollView
import kotlinx.android.synthetic.main.animation_dialog.view.*
import tk.zwander.seekbarpreference.SeekBarView

class AnimationScales(context: Context, attrs: AttributeSet) : ScrollView(context, attrs) {
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        animator_sb.apply {
            scaledProgress = Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1.0f)
            listener = object : SeekBarView.SeekBarListener {
                override fun onProgressAdded() {}
                override fun onProgressReset() {}
                override fun onProgressSubtracted() {}
                override fun onProgressChanged(newValue: Int, newScaledValue: Float) {
                    Settings.Global.putFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, newScaledValue)
                }
            }
        }

        window_sb.apply {
            scaledProgress = Settings.Global.getFloat(context.contentResolver, Settings.Global.WINDOW_ANIMATION_SCALE, 1.0f)
            listener = object : SeekBarView.SeekBarListener {
                override fun onProgressAdded() {}
                override fun onProgressReset() {}
                override fun onProgressSubtracted() {}
                override fun onProgressChanged(newValue: Int, newScaledValue: Float) {
                    Settings.Global.putFloat(context.contentResolver, Settings.Global.WINDOW_ANIMATION_SCALE, newScaledValue)
                }
            }
        }

        transition_sb.apply {
            scaledProgress = Settings.Global.getFloat(context.contentResolver, Settings.Global.TRANSITION_ANIMATION_SCALE, 1.0f)
            listener = object : SeekBarView.SeekBarListener {
                override fun onProgressAdded() {}
                override fun onProgressReset() {}
                override fun onProgressSubtracted() {}
                override fun onProgressChanged(newValue: Int, newScaledValue: Float) {
                    Settings.Global.putFloat(context.contentResolver, Settings.Global.TRANSITION_ANIMATION_SCALE, newScaledValue)
                }
            }
        }
    }
}