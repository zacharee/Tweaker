/*
 * Copyright 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zacharee1.systemuituner.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.preference.R

/**
 * Extension of [ImageView] that correctly applies maxWidth and maxHeight.
 *
 * Used by Car.
 *
 */
@SuppressLint("AppCompatCustomView")
class PreferenceImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : DisableableImageView(context, attrs!!, defStyleAttr) {
    private var mMaxWidth = Int.MAX_VALUE
    private var mMaxHeight = Int.MAX_VALUE
    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        isClickable = false
    }

    override fun setMaxWidth(maxWidth: Int) {
        mMaxWidth = maxWidth
        super.setMaxWidth(maxWidth)
    }

    override fun getMaxWidth(): Int {
        return mMaxWidth
    }

    override fun setMaxHeight(maxHeight: Int) {
        mMaxHeight = maxHeight
        super.setMaxHeight(maxHeight)
    }

    override fun getMaxHeight(): Int {
        return mMaxHeight
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var widthMeasureSpecRw = widthMeasureSpec
        var heightMeasureSpecRw = heightMeasureSpec
        val widthMode = MeasureSpec.getMode(widthMeasureSpecRw)
        if (widthMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.UNSPECIFIED) {
            val widthSize = MeasureSpec.getSize(widthMeasureSpecRw)
            val maxWidth = maxWidth
            if (maxWidth != Int.MAX_VALUE
                && (maxWidth < widthSize || widthMode == MeasureSpec.UNSPECIFIED)
            ) {
                widthMeasureSpecRw = MeasureSpec.makeMeasureSpec(maxWidth, MeasureSpec.AT_MOST)
            }
        }
        val heightMode = MeasureSpec.getMode(heightMeasureSpecRw)
        if (heightMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.UNSPECIFIED) {
            val heightSize = MeasureSpec.getSize(heightMeasureSpecRw)
            val maxHeight = maxHeight
            if (maxHeight != Int.MAX_VALUE
                && (maxHeight < heightSize || heightMode == MeasureSpec.UNSPECIFIED)
            ) {
                heightMeasureSpecRw = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST)
            }
        }
        super.onMeasure(widthMeasureSpecRw, heightMeasureSpecRw)
    }

    init {
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.PreferenceImageView, defStyleAttr, 0
        )
        maxWidth = a.getDimensionPixelSize(
            R.styleable.PreferenceImageView_maxWidth,
            Int.MAX_VALUE
        )
        maxHeight = a.getDimensionPixelSize(
            R.styleable.PreferenceImageView_maxHeight,
            Int.MAX_VALUE
        )
        a.recycle()
    }
}