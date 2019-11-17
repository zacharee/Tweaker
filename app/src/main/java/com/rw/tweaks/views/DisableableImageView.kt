package com.rw.tweaks.views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView


class DisableableImageView(context: Context, attrs: AttributeSet) : AppCompatImageView(context, attrs) {
    override fun setEnabled(enabled: Boolean) {
        if (this.isEnabled != enabled) {
            this.imageAlpha = if (enabled) 0xFF else 0x3F
        }
        super.setEnabled(enabled)
    }
}