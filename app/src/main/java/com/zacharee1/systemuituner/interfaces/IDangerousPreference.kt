package com.zacharee1.systemuituner.interfaces

import android.content.Context
import android.util.AttributeSet
import com.zacharee1.systemuituner.R

interface IDangerousPreference {
    var dangerous: Boolean
}

class DangerousPreference(context: Context, attrs: AttributeSet? = null) : IDangerousPreference {
    override var dangerous: Boolean = false

    init {
        val array = context.theme.obtainStyledAttributes(attrs, R.styleable.DangerousPreference, 0, 0)

        dangerous = array.getBoolean(R.styleable.DangerousPreference_dangerous, dangerous)
    }
}