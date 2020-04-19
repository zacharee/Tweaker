package com.zacharee1.systemuituner.util

import android.content.res.ColorStateList
import android.widget.TextView
import com.mikepenz.materialdrawer.holder.BadgeStyle
import com.mikepenz.materialdrawer.holder.DimenHolder

class SecondaryStyle : BadgeStyle() {
    override fun style(badgeTextView: TextView, colorStateList: ColorStateList?) {
        super.style(badgeTextView, colorStateList)

        badgeTextView.apply {
            setPaddingRelative(DimenHolder.fromDp(16).asPixel(badgeTextView.context), paddingTop, paddingEnd, paddingBottom)
        }
    }
}