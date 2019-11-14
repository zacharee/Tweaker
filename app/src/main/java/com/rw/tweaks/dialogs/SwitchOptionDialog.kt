package com.rw.tweaks.dialogs

import android.os.Bundle
import android.view.View
import com.rw.tweaks.R
import com.rw.tweaks.util.getSetting
import com.rw.tweaks.util.writeSetting
import kotlinx.android.synthetic.main.secure_switch.view.*

class SwitchOptionDialog : BaseOptionDialog() {
    companion object {
        const val ARG_DISABLED = "disabled"
        const val ARG_ENABLED = "enabled"

        fun newInstance(key: String, disabled: String?, enabled: String?): SwitchOptionDialog {
            return SwitchOptionDialog().apply {
                arguments = Bundle().apply {
                    putString(ARG_KEY, key)
                    putString(ARG_DISABLED, disabled)
                    putString(ARG_ENABLED, enabled)
                }
            }
        }
    }

    override val layoutRes = R.layout.secure_switch

    private val disabled by lazy { arguments?.getString(ARG_DISABLED) ?: "0" }
    private val enabled by lazy { arguments?.getString(ARG_ENABLED) ?: "1" }

    override fun onBindDialogView(view: View) {
        super.onBindDialogView(view)

        view.secure_switch.apply {
            text = preference.title
            isChecked = view.context.getSetting(type, writeKey) == enabled
            setOnCheckedChangeListener { _, isChecked ->
                val newValue = if (isChecked) enabled else disabled

                view.context.writeSetting(type, writeKey, newValue)
                notifyChanged(newValue)
            }
        }
    }
}