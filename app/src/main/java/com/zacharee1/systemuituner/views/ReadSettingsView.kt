package com.zacharee1.systemuituner.views

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doOnTextChanged
import com.zacharee1.systemuituner.util.SettingsType
import com.zacharee1.systemuituner.util.getSetting
import kotlinx.android.synthetic.main.dialog_read_setting.view.*

class ReadSettingsView(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {
    override fun onFinishInflate() {
        super.onFinishInflate()

        key_entry.doOnTextChanged { text, _, _, _ ->
            val type = SettingsType.fromValue(settings_type.selectedItemPosition)
            result.text = context.getSetting(type, text.toString()).toString()
        }
    }
}