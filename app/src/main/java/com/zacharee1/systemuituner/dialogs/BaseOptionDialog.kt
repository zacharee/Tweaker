package com.zacharee1.systemuituner.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.preference.PreferenceDialogFragmentCompat
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.interfaces.IDialogPreference
import com.zacharee1.systemuituner.interfaces.IOptionDialogCallback
import com.zacharee1.systemuituner.interfaces.ISecurePreference
import com.zacharee1.systemuituner.data.SettingsType

abstract class BaseOptionDialog : PreferenceDialogFragmentCompat() {
    companion object {
        const val ARG_LAYOUT_RES = "layout_res"
        const val ARG_KEY = "key"
    }

    internal open val layoutRes by lazy { requireArguments().getInt(ARG_LAYOUT_RES, 0) }
    internal val writeKey: String
        get() = if (preference is ISecurePreference) (preference as ISecurePreference).writeKey else preference.key
    internal val type: SettingsType
        get() = if (preference is ISecurePreference) (preference as ISecurePreference).type else SettingsType.UNDEFINED

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = ScrolledRoundedBottomSheetDialog(requireContext())

        builder.create()
        builder.findViewById<View>(android.R.id.content)?.let { onBindDialogView(it) }
        builder.setTitle(preference.dialogTitle)
        if (preference.icon != null) builder.setIcon(preference.icon)
        builder.setPositiveButton(android.R.string.ok, null)

        return builder
    }

    final override fun onCreateDialogView(context: Context): View? {
        return null
    }

    @CallSuper
    override fun onBindDialogView(view: View) {
        super.onBindDialogView(view)

        if (layoutRes != 0) {
            View.inflate(view.context, layoutRes, view.findViewById(R.id.wrapper))

            findCallbackView(view.findViewById(R.id.wrapper))?.callback = { data ->
                notifyChanged(data)
            }
        }
    }

    override fun onDialogClosed(positiveResult: Boolean) {}

    private fun findCallbackView(top: ViewGroup): IOptionDialogCallback? {
        for (i in 0 until top.childCount) {
            val child = top.getChildAt(i)

            if (child is IOptionDialogCallback) {
                return child
            }

            if (child is ViewGroup) {
                val result = findCallbackView(child)
                if (result != null) return result
            }
        }

        return null
    }

    fun notifyChanged(value: Any?) {
        (preference as IDialogPreference).onValueChanged(value, writeKey)
    }
}