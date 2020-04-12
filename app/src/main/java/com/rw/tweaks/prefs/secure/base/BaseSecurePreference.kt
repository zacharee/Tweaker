package com.rw.tweaks.prefs.secure.base

import android.content.Context
import android.util.AttributeSet
import androidx.preference.DialogPreference
import androidx.preference.PreferenceViewHolder
import com.rw.tweaks.R
import com.rw.tweaks.util.*

open class BaseSecurePreference(context: Context, attrs: AttributeSet) : DialogPreference(context, attrs), ISecurePreference by SecurePreference(context, attrs), IColorPreference by ColorPreference(context, attrs), IDialogPreference {
    override var writeKey: String? = null
        get() = field ?: key

    init {
        layoutResource = R.layout.custom_preference

        dialogMessage = summary
        dialogTitle = title
    }

    override fun onAttached() {
        super.onAttached()

        init(this)
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        bindVH(holder)
    }

    override fun onValueChanged(newValue: Any?, key: String) {
        context.prefManager.putString(writeKey!!, newValue.toString())
        context.writeSetting(type, writeKey, newValue.toString().toInt())
    }
}