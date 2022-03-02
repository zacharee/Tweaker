package com.zacharee1.systemuituner.dialogs

import android.content.Context
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.util.buildNonResettablePreferences
import com.zacharee1.systemuituner.util.resetAll

class ResetDialog(context: Context) : RoundedBottomSheetDialog(context) {
    init {
        setTitle(R.string.reset)
        setMessage(context.resources.getString(R.string.reset_confirm,
            context.buildNonResettablePreferences().joinToString(prefix = "\n- ", separator = "\n- ")))
        setPositiveButton(R.string.reset) { _, _ ->
            context.resetAll()
            dismiss()
        }
        setNegativeButton(android.R.string.cancel, null)
    }
}