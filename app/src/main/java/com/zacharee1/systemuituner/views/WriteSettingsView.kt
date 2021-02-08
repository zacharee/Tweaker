package com.zacharee1.systemuituner.views

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.zacharee1.systemuituner.databinding.DialogWriteSettingBinding
import com.zacharee1.systemuituner.util.SettingsType
import com.zacharee1.systemuituner.util.writeSetting

class WriteSettingsView(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {
    private val binding by lazy { DialogWriteSettingBinding.bind(this) }

    override fun onFinishInflate() {
        super.onFinishInflate()

        binding.apply.setOnClickListener {
            val key = binding.keyEntry.text?.toString()
            val value = binding.valueEntry.text?.toString()
            val type = SettingsType.fromValue(binding.settingsType.selectedItemPosition)

            if (!key.isNullOrBlank()) {
                context.writeSetting(type, key, value)
            }
        }
    }
}