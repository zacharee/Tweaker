package com.zacharee1.systemuituner.dialogs

import android.os.Bundle
import android.view.View
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.util.getSetting
import com.zacharee1.systemuituner.util.prefManager
import com.zacharee1.systemuituner.util.writeSetting
import kotlinx.android.synthetic.main.seekbar_dialog.view.*
import tk.zwander.seekbarpreference.SeekBarView

class SeekBarOptionDialog : BaseOptionDialog(), SeekBarView.SeekBarListener {
    companion object {
        const val ARG_MIN = "minValue"
        const val ARG_MAX = "maxValue"
        const val ARG_UNITS = "units"
        const val ARG_DEFAULT = "defaultValue"
        const val ARG_SCALE = "scale"
        const val ARG_FOR_SECURE = "for_secure"
        const val ARG_INITIAL_VALUE = "initial_value"

        fun newInstance(key: String, min: Int = 0, max: Int = 100, default: Int = min, units: String? = null, scale: Float = 1.0f, initialValue: Int = 100): SeekBarOptionDialog {
            return SeekBarOptionDialog().apply {
                arguments = Bundle().apply {
                    putString(ARG_KEY, key)
                    putInt(ARG_MIN, min)
                    putInt(ARG_MAX, max)
                    putInt(ARG_DEFAULT, default)
                    putString(ARG_UNITS, units)
                    putFloat(ARG_SCALE, scale)
                    putInt(ARG_INITIAL_VALUE, initialValue)
                }
            }
        }
    }

    override val layoutRes = R.layout.seekbar_dialog

    private val min by lazy { arguments?.getInt(ARG_MIN, 0) ?: 0 }
    private val max by lazy { arguments?.getInt(ARG_MAX, 100) ?: 100 }
    private val default by lazy { arguments?.getInt(ARG_DEFAULT, min) ?: min }
    private val units by lazy { arguments?.getString(ARG_UNITS) }
    private val scale by lazy { arguments?.getFloat(ARG_SCALE, 1.0f) ?: 1.0f }
    private val initialValue by lazy { arguments?.getInt(ARG_INITIAL_VALUE, default) ?: default }

    override fun onBindDialogView(view: View) {
        super.onBindDialogView(view)

        view.seekbar_view.onBind(min, max, initialValue, default, scale, units, "",this@SeekBarOptionDialog)
    }


    override fun onProgressAdded() {}
    override fun onProgressReset() {}
    override fun onProgressSubtracted() {}
    override fun onProgressChanged(newValue: Int, newScaledValue: Float) {
        notifyChanged(if (scale == 1f) newValue else newScaledValue)
    }
}