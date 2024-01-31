package com.zacharee1.systemuituner.interfaces

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.StateListDrawable
import android.util.AttributeSet
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.recyclerview.widget.RecyclerView
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.databinding.CustomPreferenceBinding

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

            iconColor = array.getColor(R.styleable.ColorPreference_icon_color, iconColor)
        }
    }

    @SuppressLint("NewApi")
    override fun bindVH(holder: RecyclerView.ViewHolder) {
        val binding = CustomPreferenceBinding.bind(holder.itemView)

        binding.iconFrame.apply {
            (background as StateListDrawable).apply {
                val drawable = getStateDrawable(1)

                if (iconColor != Int.MIN_VALUE) {
                    drawable?.setTint(iconColor)
//                    drawable?.setColorFilterCompat(iconColor, PorterDuff.Mode.SCREEN)
                } else {
                    drawable?.setTintList(null)
//                    drawable?.clearColorFilter()
                }
            }
        }

        binding.icon.apply {
            if (iconColor != Int.MIN_VALUE) {
                val l = Color(iconColor).luminance()

                imageTintList = ColorStateList.valueOf(
                    if (l > 0.5) {
                        Color.Black
                    } else {
                        Color.White
                    }.toArgb()
                )
            }
        }
    }
}