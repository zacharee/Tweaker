package com.rw.tweaks.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.preference.PreferenceDialogFragmentCompat
import com.rw.tweaks.R
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
        return super.onCreateDialog(savedInstanceState).also {
            it.window.setWindowAnimations(R.style.DialogTheme)
        }
    }

    override fun onCreateDialogView(context: Context?): View {
        return View.inflate(context, R.layout.base_dialog_layout, null)
    }

    @CallSuper
    override fun onBindDialogView(view: View) {
        super.onBindDialogView(view)

         View.inflate(view.context, layoutRes, view.wrapper)
    }

    override fun onDialogClosed(positiveResult: Boolean) {}

    fun notifyChanged(value: Any?) {
        (preference as ISecurePreference).onValueChanged(value, writeKey)
    }
}