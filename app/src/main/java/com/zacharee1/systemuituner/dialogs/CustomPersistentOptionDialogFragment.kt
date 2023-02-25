package com.zacharee1.systemuituner.dialogs

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.Spinner
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.data.CustomPersistentOption
import com.zacharee1.systemuituner.data.SettingsType
import com.zacharee1.systemuituner.util.prefManager
import com.zacharee1.systemuituner.util.writeSetting
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class CustomPersistentOptionDialogFragment : DialogFragment(), CoroutineScope by MainScope() {
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
        val builder = ScrolledRoundedBottomSheetDialog(requireContext())

        builder.findViewById<View>(android.R.id.content)?.let {
            View.inflate(it.context, R.layout.custom_persistent_option_dialog, it.findViewById(R.id.wrapper))
        }
        builder.setTitle(if (isEditing) R.string.edit_custom_item else R.string.add_custom_item)
        builder.findViewById<View>(R.id.wrapper)?.apply {
            if (isEditing) {
                findViewById<TextInputEditText>(R.id.label_entry).setText(initialLabel)
                findViewById<TextInputEditText>(R.id.key_entry).setText(initialKey)
                findViewById<TextInputEditText>(R.id.value_entry).setText(initialValue)
                if (initialType.value != -1) findViewById<Spinner>(R.id.settings_type).setSelection(initialType.value)
            }
        }
        builder.setPositiveButton(android.R.string.ok) { _, _ ->
            this.dialog?.findViewById<View>(android.R.id.content)?.apply {
                val label = findViewById<TextInputEditText>(R.id.label_entry).text?.toString()
                    ?: return@apply
                val key =
                    findViewById<TextInputEditText>(R.id.key_entry).text?.toString() ?: return@apply
                val value = findViewById<TextInputEditText>(R.id.value_entry).text?.toString()
                val type =
                    SettingsType.fromValue(findViewById<Spinner>(R.id.settings_type).selectedItemPosition)

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
                launch {
                    context.writeSetting(type, key, value)
                }
            }
            dismiss()
        }
        builder.setNegativeButton(android.R.string.cancel) { _, _ ->
            dismiss()
        }

        return builder
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        dismiss()
    }
}