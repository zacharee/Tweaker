package com.rw.tweaks.prefs.nav

import android.content.Context
import android.util.AttributeSet

class IndentedNavigationPreference(context: Context, attributeSet: AttributeSet) : NavigationPreference(context, attributeSet) {
    override val startMargin: StartMargin = StartMargin.INDENT
    override val useDividers: Boolean = false
}