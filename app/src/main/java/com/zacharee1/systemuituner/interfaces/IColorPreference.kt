package com.zacharee1.systemuituner.interfaces

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.StateListDrawable
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.databinding.CustomPreferenceBinding
import com.zacharee1.systemuituner.util.setColorFilterCompat

interface IColorPreference {
    var iconColor: Int

    fun bindVH(holder: RecyclerView.ViewHolder)
}

class ColorPreference(context: Context, attrs: AttributeSet?) :
    IColorPreference {
    override var iconColor: Int = Int.MIN_VALUE

    init {
        if (attrs != null) {
            val array = context.theme.obtainStyledAttributes(attrs, R.styleable.ColorPreference, 0, 0)

            iconColor = array.getColor(R.styleable.ColorPreference_icon_color, Int.MIN_VALUE)
        }
    }

    override fun bindVH(holder: RecyclerView.ViewHolder) {
        val binding = CustomPreferenceBinding.bind(holder.itemView)

        binding.iconFrame.apply {
            (background as StateListDrawable).apply {
                val drawable = getStateDrawable(1)

                if (iconColor != Int.MIN_VALUE) {
                    drawable?.setColorFilterCompat(iconColor, PorterDuff.Mode.SRC_ATOP)
                } else {
                    drawable?.clearColorFilter()
                }
            }
        }
    }
}