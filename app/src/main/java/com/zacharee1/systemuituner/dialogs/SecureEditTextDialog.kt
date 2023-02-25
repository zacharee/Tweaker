package com.zacharee1.systemuituner.dialogs

import android.os.Bundle
import android.view.View
import android.widget.EditText
import com.google.android.material.textfield.TextInputLayout
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.prefs.secure.SecureEditTextPreference
import com.zacharee1.systemuituner.util.defaultValue
import kotlinx.coroutines.launch

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

        view.findViewById<TextInputLayout>(R.id.edit_wrapper).apply {
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
            launch {
                editTextPreference.onValueChanged(text, writeKey)
            }
        }
    }

    override fun onDialogClosed(positiveResult: Boolean) {}
}