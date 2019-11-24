package com.rw.tweaks.views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView


open class DisableableImageView(context: Context, attrs: AttributeSet, defStyleAttr: Int) : AppCompatImageView(context, attrs, defStyleAttr) {
    override fun setEnabled(enabled: Boolean) {
        if (this.isEnabled != enabled) {
            this.imageAlpha = if (enabled) 0xFF else 0x3F
        }
        isClickable = enabled
        super.setEnabled(enabled)
    }
}