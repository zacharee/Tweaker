package com.rw.tweaks.views

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import com.rw.tweaks.util.mainHandler

class ZeroHeightTextView(context: Context, attrs: AttributeSet) : AppCompatTextView(context, attrs) {
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        mainHandler.post {
            updateHeight()
        }
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        super.setText(text, type)

        mainHandler.post {
            updateHeight()
        }
    }

    private fun updateHeight() {
        layoutParams?.apply {
            height = if (text.isNullOrBlank()) 0
            else ViewGroup.LayoutParams.WRAP_CONTENT

            layoutParams = this
        }
    }
}