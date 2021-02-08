package com.zacharee1.systemuituner.views

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doOnTextChanged
import com.zacharee1.systemuituner.databinding.DialogReadSettingBinding
import com.zacharee1.systemuituner.util.SettingsType
import com.zacharee1.systemuituner.util.getSetting

class ReadSettingsView(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {
    private val binding by lazy { DialogReadSettingBinding.bind(this) }

    override fun onFinishInflate() {
        super.onFinishInflate()

        binding.keyEntry.doOnTextChanged { text, _, _, _ ->
            val type = SettingsType.fromValue(binding.settingsType.selectedItemPosition)
            binding.result.text = context.getSetting(type, text.toString()).toString()
        }
    }
}