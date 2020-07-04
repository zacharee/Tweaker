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
import kotlinx.android.synthetic.main.base_dialog_layout.*
import kotlinx.android.synthetic.main.base_dialog_layout.view.*
import kotlinx.android.synthetic.main.custom_persistent_option_dialog.view.*
import kotlinx.android.synthetic.main.custom_persistent_option_dialog.view.key_entry
import kotlinx.android.synthetic.main.custom_persistent_option_dialog.view.settings_type

class CustomPersistentOptionDialogFragment : DialogFragment() {
    companion object {
        const val ARG_LABEL = "label"
        const val ARG_KEY = "key"
        const val ARG_VALUE = "value"
        const val ARG_TYPE = "type"
        const val ARG_IS_EDITING = "is_editing"

        fun forEdit(label: String, key: String, value: String?, type: SettingsType): CustomPersistentOptionDialogFragment {
            return CustomPersistentOptionDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_LABEL, label)
                    putString(ARG_KEY, key)
                    putString(ARG_VALUE, value)
                    putString(ARG_TYPE, type.toString())
                    putBoolean(ARG_IS_EDITING, true)
                }
            }
        }
    }

    private val initialLabel by lazy { arguments?.getString(ARG_LABEL) }
    private val initialKey by lazy { arguments?.getString(ARG_KEY) }
    private val initialValue by lazy { arguments?.getString(ARG_VALUE) }
    private val initialType by lazy { SettingsType.fromString(arguments?.getString(ARG_TYPE) ?: SettingsType.UNDEFINED_LITERAL) }
    private val isEditing by lazy { arguments?.getBoolean(ARG_IS_EDITING) ?: false }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = RoundedBottomSheetDialog(requireContext())

        builder.findViewById<View>(android.R.id.content)?.let {
            View.inflate(it.context, R.layout.custom_persistent_option_dialog, it.wrapper)
        }
        builder.setTitle(R.string.add_custom_item)
        builder.wrapper.apply {
            if (isEditing) {
                label_entry?.setText(initialLabel)
                key_entry?.setText(initialKey)
                value_entry?.setText(initialValue)
                if (initialType.value != -1) settings_type?.setSelection(initialType.value)
            }
        }
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
                        removeAll {
                            if (isEditing) {
                                it.key == initialKey && it.type == initialType
                            } else {
                                it.key == key && it.type == type
                            }
                        }
                        add(item)
                    }
                    saveOption(type, key, value)
                }
            }
            dismiss()
        })
        builder.setNegativeButton(android.R.string.cancel, DialogInterface.OnClickListener { _, _ ->
            dismiss()
        })
        builder.setOnCancelListener {
            dismiss()
        }

        return builder
    }
}