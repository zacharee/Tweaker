package com.rw.tweaks.dialogs

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.preference.PreferenceDialogFragmentCompat
import com.rw.tweaks.R
import com.rw.tweaks.data.CustomBlacklistItemInfo
import com.rw.tweaks.util.prefManager
import kotlinx.android.synthetic.main.base_dialog_layout.view.*
import kotlinx.android.synthetic.main.custom_blacklist_dialog.view.*

class CustomBlacklistItemDialogFragment : PreferenceDialogFragmentCompat() {
    companion object {
        fun newInstance(key: String): CustomBlacklistItemDialogFragment {
            val instance = CustomBlacklistItemDialogFragment()
            val args = Bundle()

            args.putString(BaseOptionDialog.ARG_KEY, key)

            instance.arguments = args

            return instance
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = RoundedBottomSheetDialog(requireContext())

        builder.findViewById<View>(android.R.id.content)?.let { onBindDialogView(it) }
        builder.setTitle(preference.title)
        builder.setPositiveButton(android.R.string.ok, DialogInterface.OnClickListener { dialog, which ->
            onClick(dialog, which)
            dismiss()
        })
        builder.setNegativeButton(android.R.string.cancel, DialogInterface.OnClickListener { dialog, which ->
            onClick(dialog, which)
            dismiss()
        })

        return builder
    }

    override fun onBindDialogView(view: View) {
        super.onBindDialogView(view)

        View.inflate(view.context, R.layout.custom_blacklist_dialog, view.wrapper)
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        dialog?.findViewById<View>(android.R.id.content)?.apply {
            val label = this.label?.editText?.text?.toString()
            val key = this.key?.editText?.text?.toString() ?: return

            val item = CustomBlacklistItemInfo(label, key)

            context.prefManager.apply {
                customBlacklistItems = customBlacklistItems.apply {
                    remove(item)
                    add(item)
                }
            }
        }
    }
}