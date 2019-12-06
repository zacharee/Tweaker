package com.rw.tweaks.dialogs

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.rw.tweaks.R

class AnimatedRoundedMaterialAlertDialogBuilder : MaterialAlertDialogBuilder {
    constructor(context: Context) : super(context)
    constructor(context: Context, overrideThemeResId: Int) : super(context, overrideThemeResId)

    init {
        background = context.getDrawable(R.drawable.rounded_rect)
    }

    override fun create(): AlertDialog {
        return super.create().apply {
            window.setTheme(R.style.DialogTheme)
            window.setWindowAnimations(R.style.DialogTheme)
        }
    }
}