package com.rw.tweaks.dialogs

import android.content.Context
import android.content.DialogInterface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.rw.tweaks.R
import kotlinx.android.synthetic.main.base_dialog_layout.*

class RoundedBottomSheetDialog(context: Context) : BottomSheetDialog(context, R.style.BottomSheetTheme), View.OnClickListener {
    init {
        setContentView(R.layout.base_dialog_layout)

        positive_button.setOnClickListener(this)
        negative_button.setOnClickListener(this)
    }

    private var positiveListener: DialogInterface.OnClickListener? = null
    private var negativeListener: DialogInterface.OnClickListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val maxWidth = context.resources.getDimensionPixelSize(R.dimen.max_bottom_sheet_width)
        val screenWidth = context.resources.displayMetrics.widthPixels

        window.setLayout(if (screenWidth > maxWidth) maxWidth else ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    override fun setTitle(titleId: Int) {
        super.setTitle(titleId)

        findViewById<TextView>(android.R.id.title)?.setText(titleId)
    }

    override fun setTitle(title: CharSequence?) {
        super.setTitle(title)

        findViewById<TextView>(android.R.id.title)?.text = title
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.positive_button -> {
                if (positiveListener != null) {
                    positiveListener?.onClick(this, DialogInterface.BUTTON_POSITIVE)
                } else {
                    dismiss()
                }
            }

            R.id.negative_button -> {
                if (negativeListener != null) {
                    negativeListener?.onClick(this, DialogInterface.BUTTON_NEGATIVE)
                } else {
                    dismiss()
                }
            }
        }
    }

    fun setIcon(icon: Int) {
        setIcon(context.getDrawable(icon))
    }

    fun setIcon(icon: Drawable) {
        this.icon.setImageDrawable(icon)
    }

    fun setPositiveButton(text: Int, listener: DialogInterface.OnClickListener) {
        setPositiveButton(context.resources.getText(text), listener)
    }

    fun setPositiveButton(text: CharSequence?, listener: DialogInterface.OnClickListener) {
        positive_button.text = text
        positive_button.isVisible = text != null

        positiveListener = listener
    }

    fun setNegativeButton(text: Int, listener: DialogInterface.OnClickListener?) {
        setNegativeButton(context.resources.getText(text), listener)
    }

    fun setNegativeButton(text: CharSequence?, listener: DialogInterface.OnClickListener?) {
        negative_button.text = text
        negative_button.isVisible = text != null

        negativeListener = listener
    }

    fun setMessage(msg: Int) {
        setMessage(context.getText(msg))
    }

    fun setMessage(msg: CharSequence?) {
        findViewById<TextView>(android.R.id.message)?.text = msg
    }
}