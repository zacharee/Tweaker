package com.zacharee1.systemuituner.dialogs

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.util.getSetting
import kotlinx.android.synthetic.main.secure_switch.view.*

class SwitchOptionDialog : BaseOptionDialog() {
    companion object {
        const val ARG_DISABLED = "disabled"
        const val ARG_ENABLED = "enabled"
        const val ARG_CHECKED = "checked"

        fun newInstance(key: String, disabled: String?, enabled: String?, shouldBeChecked: Boolean? = null): SwitchOptionDialog {
            return SwitchOptionDialog().apply {
                arguments = Bundle().apply {
                    putString(ARG_KEY, key)
                    putString(ARG_DISABLED, disabled)
                    putString(ARG_ENABLED, enabled)
                    putString(ARG_CHECKED, shouldBeChecked?.toString())
                }
            }
        }
    }

    override val layoutRes = R.layout.secure_switch

    private val disabled by lazy { arguments?.getString(ARG_DISABLED) ?: "0" }
    private val enabled by lazy { arguments?.getString(ARG_ENABLED) ?: "1" }
    private val shouldBeChecked by lazy { arguments?.getString(ARG_CHECKED)?.toBoolean() ?: false }

    override fun onBindDialogView(view: View) {
        super.onBindDialogView(view)

        view.secure_switch.apply {
            text = preference.title
            isChecked = shouldBeChecked
            setOnCheckedChangeListener { _, isChecked ->
                val newValue = if (isChecked) enabled else disabled

                notifyChanged(newValue)
            }
        }
    }
}