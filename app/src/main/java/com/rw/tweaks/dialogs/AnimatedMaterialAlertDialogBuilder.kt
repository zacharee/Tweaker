package com.rw.tweaks.dialogs

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.rw.tweaks.R

class AnimatedMaterialAlertDialogBuilder(context: Context) : MaterialAlertDialogBuilder(context) {
    override fun create(): AlertDialog {
        return super.create().also {
            it.window.setWindowAnimations(R.style.DialogTheme)
            it.window.setBackgroundDrawableResource(R.drawable.normal_dialog_background)
        }
    }
}