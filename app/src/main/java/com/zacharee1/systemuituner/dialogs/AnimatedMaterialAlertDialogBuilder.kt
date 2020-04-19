package com.zacharee1.systemuituner.dialogs

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.zacharee1.systemuituner.R

class AnimatedMaterialAlertDialogBuilder(context: Context) : MaterialAlertDialogBuilder(context) {
    override fun create(): AlertDialog {
        return super.create().also {
            it.window.setWindowAnimations(R.style.DialogTheme)
            it.window.setBackgroundDrawableResource(R.drawable.normal_dialog_background)
        }
    }
}