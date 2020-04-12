package com.rw.tweaks.dialogs

import android.os.Bundle
import android.view.View
import android.widget.EditText
import com.rw.tweaks.prefs.secure.SecureEditTextPreference
import com.rw.tweaks.util.defaultValue
import kotlinx.android.synthetic.main.better_edittext_dialog.view.*

class SecureEditTextDialog : BaseOptionDialog() {
    private var editText: EditText? = null
    private var text: CharSequence? = null

    override val layoutRes: Int
        get() = preference.dialogLayoutResource

    private val editTextPreference: SecureEditTextPreference
        get() = preference as SecureEditTextPreference

    companion object {
        private const val SAVE_STATE_TEXT = "EditTextPreferenceDialogFragment.text"

        fun newInstance(key: String): SecureEditTextDialog {
            val fragment = SecureEditTextDialog()
            val b = Bundle(1)
            b.putString(ARG_KEY, key)
            fragment.arguments = b
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        text = if (savedInstanceState == null) {
            editTextPreference.text
        } else {
            savedInstanceState.getCharSequence(SAVE_STATE_TEXT)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putCharSequence(SAVE_STATE_TEXT, text)
    }

    override fun onBindDialogView(view: View) {
        super.onBindDialogView(view)
        editText = view.findViewById(android.R.id.edit)
        editText?.inputType = editTextPreference.inputType
        editText?.setText(text)

        view.edit_wrapper.apply {
            setStartIconOnClickListener {
                apply(preference.defaultValue?.toString())
                editText?.setText(preference.defaultValue?.toString())
            }

            setEndIconOnClickListener {
                apply(editText?.text?.toString())
            }

            hint = preference.title
        }
    }

    private fun apply(text: String?) {
        if (preference.callChangeListener(text)) {
            editTextPreference.text = text
            editTextPreference.onValueChanged(text, writeKey!!)
        }
    }

    override fun onDialogClosed(positiveResult: Boolean) {}
}