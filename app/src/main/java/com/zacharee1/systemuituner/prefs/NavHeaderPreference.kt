package com.zacharee1.systemuituner.prefs

import android.content.Context
import android.util.AttributeSet
import androidx.preference.Preference
import com.zacharee1.systemuituner.R

class NavHeaderPreference(context: Context, attrs: AttributeSet) : Preference(context, attrs) {
    init {
        layoutResource = R.layout.widget_only_preference
        widgetLayoutResource = R.layout.drawer_header
        isEnabled = false
    }
}