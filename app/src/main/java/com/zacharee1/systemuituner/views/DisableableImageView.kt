package com.zacharee1.systemuituner.views

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView


open class DisableableImageView : AppCompatImageView {
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var alphaAnim: ValueAnimator? = null

    override fun setEnabled(enabled: Boolean) {
        if (this.isEnabled != enabled) {
            alphaAnim?.cancel()

            alphaAnim = ValueAnimator.ofInt(this.imageAlpha, if (enabled) 0xFF else 0x3F)
                .apply {
                    addUpdateListener {
                        this@DisableableImageView.imageAlpha = it.animatedValue.toString().toInt()
                    }
                    start()
                }
        }
        isClickable = enabled
        super.setEnabled(enabled)
    }
}