package com.zacharee1.systemuituner.views

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import com.google.android.material.card.MaterialCardView
import com.zacharee1.systemuituner.util.dpAsPx

open class RoundedFrameCardView(context: Context, attrs: AttributeSet) : MaterialCardView(context, attrs) {
    init {
        radius = context.dpAsPx(8).toFloat()
        strokeWidth = context.dpAsPx(0.75)
        strokeColor = run {
            val a = intArrayOf(android.R.attr.textColorSecondary)
            val array = context.obtainStyledAttributes(a)
            context.getColor(array.getResourceId(0, android.R.color.transparent)).also { array.recycle() }
        }
        setCardBackgroundColor(Color.TRANSPARENT)
        elevation = 0f

        val padding = context.dpAsPx(8)
        setContentPadding(padding, padding, padding, padding)
    }
}