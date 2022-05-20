package com.zacharee1.systemuituner.views

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import com.jaredrummler.android.colorpicker.ColorShape
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.databinding.TouchwizNavigationBarColorDialogBinding
import com.zacharee1.systemuituner.interfaces.IOptionDialogCallback
import com.zacharee1.systemuituner.util.*

class TouchWizNavigationBarColorView(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs), ColorPickerDialogListener, IOptionDialogCallback {
    override var callback: ((data: Any?) -> Unit)? = null

    private val dialog: ColorPickerDialog = ColorPickerDialog.newBuilder()
        .setDialogType(ColorPickerDialog.TYPE_PRESETS)
        .setDialogTitle(R.string.option_touchwiz_navbar_color)
        .setColorShape(ColorShape.CIRCLE)
        .setAllowPresets(true)
        .setAllowCustom(true)
        .setShowAlphaSlider(true)
        .setShowColorShades(true)
        .setColor(context.getSetting(SettingsType.GLOBAL, "navigationbar_color")?.toIntOrNull() ?: Color.WHITE)
        .create()

    private val binding by lazy { TouchwizNavigationBarColorDialogBinding.bind(this) }

    override fun onFinishInflate() {
        super.onFinishInflate()

        dialog.setColorPickerDialogListener(this)
        binding.setColor.setOnClickListener {
            getActivity().supportFragmentManager
                .beginTransaction()
                .add(dialog, null)
                .commitAllowingStateLoss()
        }
        binding.currentColor.color = context.getSetting(SettingsType.GLOBAL, "navigationbar_color")?.toIntOrNull() ?: Color.WHITE
    }

    override fun onColorReset(dialogId: Int) {
        persistColor(null)
    }

    override fun onColorSelected(dialogId: Int, color: Int) {
        persistColor(color)
    }

    override fun onDialogDismissed(dialogId: Int) {}

    private fun persistColor(color: Int?) {
        binding.currentColor.color = color ?: Color.WHITE
        callback?.invoke(color)
    }

    private fun getActivity(): FragmentActivity {
        val context = context
        if (context is FragmentActivity) {
            return context
        } else if (context is ContextWrapper) {
            val baseContext = context.baseContext
            if (baseContext is FragmentActivity) {
                return baseContext
            }
        }
        throw IllegalStateException("Error getting activity from context: $context")
    }
}