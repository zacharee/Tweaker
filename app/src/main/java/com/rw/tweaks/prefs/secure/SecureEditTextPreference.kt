package com.rw.tweaks.prefs.secure

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import com.rw.tweaks.R
import com.rw.tweaks.prefs.secure.base.BaseSecurePreference
import com.rw.tweaks.util.getSetting
import com.rw.tweaks.util.prefManager
import com.rw.tweaks.util.writeSetting

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

    override fun onValueChanged(newValue: Any?, key: String?) {
        super.onValueChanged(newValue, key)

        context.prefManager.putString(writeKey!!, newValue.toString())
        context.writeSetting(type, writeKey, text)
    }
}