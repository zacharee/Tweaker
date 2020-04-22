package com.zacharee1.systemuituner.views

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.util.AttributeSet
import androidx.fragment.app.FragmentActivity
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import com.jaredrummler.android.colorpicker.ColorShape
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.util.*
import kotlinx.android.synthetic.main.touchwiz_navigation_bar_color_dialog.view.*


class TouchWizNavigationBarColorView(context: Context, attrs: AttributeSet) : RoundedFrameCardView(context, attrs), ColorPickerDialogListener {
    private val dialog: ColorPickerDialog = ColorPickerDialog.newBuilder()
        .setDialogType(ColorPickerDialog.TYPE_PRESETS)
        .setDialogTitle(R.string.option_touchwiz_navbar_color)
        .setColorShape(ColorShape.CIRCLE)
        .setAllowPresets(true)
        .setAllowCustom(true)
        .setShowAlphaSlider(true)
        .setShowColorShades(true)
        .setColor((context.getSetting(SettingsType.GLOBAL, "navigationbar_color") ?: "${Color.WHITE}").toInt())
        .create()

    override fun onFinishInflate() {
        super.onFinishInflate()

        dialog.setColorPickerDialogListener(this)
        set_color.setOnClickListener {
            getActivity().supportFragmentManager
                .beginTransaction()
                .add(dialog, null)
                .commitAllowingStateLoss()
        }
        current_color.color = context.getSetting(SettingsType.GLOBAL, "navigationbar_color")?.toInt() ?: Color.WHITE
    }

    override fun onColorReset(dialogId: Int) {
        persistColor(null)
    }

    override fun onColorSelected(dialogId: Int, color: Int) {
        persistColor(color)
    }

    override fun onDialogDismissed(dialogId: Int) {}

    private fun persistColor(color: Int?) {
        current_color.color = color ?: Color.WHITE
        context.prefManager.putString("navigationbar_color", color?.toString())
        context.prefManager.putString("navigationbar_current_color", color?.toString())
        context.writeGlobal("navigationbar_color", color)
        context.writeGlobal("navigationbar_current_color", color)
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