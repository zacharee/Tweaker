package com.zacharee1.systemuituner.prefs.secure

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.prefs.secure.base.BaseSecurePreference
import com.zacharee1.systemuituner.util.getSetting

class SecureEditTextPreference(context: Context, attrs: AttributeSet) : BaseSecurePreference(context, attrs) {
    var inputType: Int = InputType.TYPE_CLASS_TEXT

    var text: CharSequence? = null

    init {
        val array = context.theme.obtainStyledAttributes(attrs, R.styleable.SecureEditTextPreference, 0, 0)
        inputType = array.getInt(R.styleable.SecureEditTextPreference_android_inputType, inputType)
        array.recycle()

        dialogLayoutResource = R.layout.better_edittext_dialog
    }

    override fun onSetInitialValue(defaultValue: Any?) {
        this.text = context.getSetting(type, writeKey)
    }
}