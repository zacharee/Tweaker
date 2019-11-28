package com.rw.tweaks.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.preference.EditTextPreferenceDialogFragmentCompat
import androidx.preference.PreferenceDialogFragmentCompat
import com.rw.tweaks.R

class SecureEditTextDialog : EditTextPreferenceDialogFragmentCompat() {
    companion object {
        fun newInstance(key: String): EditTextPreferenceDialogFragmentCompat? {
            val fragment = SecureEditTextDialog()
            val b = Bundle(1)
            b.putString(PreferenceDialogFragmentCompat.ARG_KEY, key)
            fragment.arguments = b
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).also {
            it.window.setWindowAnimations(R.style.DialogTheme)
        }
    }
}