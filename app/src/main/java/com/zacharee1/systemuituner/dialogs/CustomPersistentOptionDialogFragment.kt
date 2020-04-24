package com.zacharee1.systemuituner.dialogs

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.data.CustomPersistentOption
import com.zacharee1.systemuituner.util.SettingsType
import com.zacharee1.systemuituner.util.prefManager
import kotlinx.android.synthetic.main.base_dialog_layout.view.*
import kotlinx.android.synthetic.main.custom_persistent_option_dialog.view.*
import kotlinx.android.synthetic.main.custom_persistent_option_dialog.view.key_entry
import kotlinx.android.synthetic.main.custom_persistent_option_dialog.view.settings_type

class CustomPersistentOptionDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = RoundedBottomSheetDialog(requireContext())

        builder.findViewById<View>(android.R.id.content)?.let {
            View.inflate(it.context, R.layout.custom_persistent_option_dialog, it.wrapper)
        }
        builder.setTitle(R.string.add_custom_item)
        builder.setPositiveButton(android.R.string.ok, DialogInterface.OnClickListener { _, _ ->
            this.dialog?.findViewById<View>(android.R.id.content)?.apply {
                val label = this.label_entry?.text?.toString() ?: return@apply
                val key = this.key_entry?.text?.toString() ?: return@apply
                val value = this.value_entry?.text?.toString()
                val type = SettingsType.fromValue(settings_type.selectedItemPosition)

                if (key.isBlank()) return@apply

                val item = CustomPersistentOption(label, value, type, key)

                context.prefManager.apply {
                    customPersistentOptions = customPersistentOptions.apply {
                        removeAll { it.key == key && it.type == type }
                        add(item)
                    }
                    saveOption(type, key, value)
                }
            }
            dismiss()
        })
        builder.setNegativeButton(android.R.string.cancel, DialogInterface.OnClickListener { dialog, which ->
            dismiss()
        })
        builder.setOnCancelListener {
            dismiss()
        }

        return builder
    }
}