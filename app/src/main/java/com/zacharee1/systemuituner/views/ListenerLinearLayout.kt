package com.zacharee1.systemuituner.views

import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet
import android.widget.LinearLayout

class ListenerLinearLayout(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    var onConfigurationChangedListener: ((newConfig: Configuration) -> Unit)? = null

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        onConfigurationChangedListener?.invoke(newConfig)
    }
}