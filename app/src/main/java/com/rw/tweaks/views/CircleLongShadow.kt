package com.rw.tweaks.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.util.AttributeSet
import com.github.florent37.longshadow.LongShadow

class CircleLongShadow(context: Context, attrs: AttributeSet) : LongShadow(context, attrs) {
    private val path = Path()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        // compute the path
        val halfWidth = w / 2f
        val halfHeight = h / 2f
        path.reset()
        path.addCircle(halfWidth, halfHeight, halfWidth.coerceAtMost(halfHeight), Path.Direction.CW)
        path.close()
    }

    override fun onDraw(canvas: Canvas) {
        val save = canvas.save()
        canvas.clipPath(path)
        super.onDraw(canvas)
        canvas.restoreToCount(save)
    }
}