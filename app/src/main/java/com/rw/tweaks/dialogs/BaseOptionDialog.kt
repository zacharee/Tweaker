package com.rw.tweaks.dialogs

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.preference.PreferenceDialogFragmentCompat
import com.rw.tweaks.util.ISecurePreference
import com.rw.tweaks.util.SettingsType
import kotlinx.android.synthetic.main.base_dialog_layout.view.*

abstract class BaseOptionDialog : PreferenceDialogFragmentCompat() {
    companion object {
        const val ARG_LAYOUT_RES = "layout_res"
        const val ARG_KEY = "key"
    }

    internal open val layoutRes by lazy { arguments!!.getInt(ARG_LAYOUT_RES, 0) }
    internal val writeKey: String?
        get() = (preference as ISecurePreference).writeKey
    internal val type: SettingsType
        get() = (preference as ISecurePreference).type

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = RoundedBottomSheetDialog(requireContext())

        builder.findViewById<View>(android.R.id.content)?.let { onBindDialogView(it) }
        builder.setTitle(preference.dialogTitle)
        builder.setIcon(preference.icon)
        builder.setPositiveButton(android.R.string.ok, DialogInterface.OnClickListener { _, _ ->
            dismiss()
        })

        return builder
    }

    final override fun onCreateDialogView(context: Context?): View? {
        return null
    }

    @CallSuper
    override fun onBindDialogView(view: View) {
        super.onBindDialogView(view)

        if (layoutRes != 0) {
            View.inflate(view.context, layoutRes, view.wrapper)
        }
    }

    override fun onDialogClosed(positiveResult: Boolean) {}

    fun notifyChanged(value: Any?) {
        (preference as ISecurePreference).onValueChanged(value, writeKey)
    }
}