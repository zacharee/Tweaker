package com.zacharee1.systemuituner.dialogs

import android.content.Context
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.util.launchUrl

class OneUITunerDialog(context: Context) : ScrolledRoundedBottomSheetDialog(context) {
    init {
        setTitle(R.string.oneui_tuner)
        setMessage(R.string.oneui_tuner_desc)
        setPositiveButton(R.string.check_it_out) { _, _ ->
            context.launchUrl("https://zwander.dev/dialog-oneuituner")
            dismiss()
        }
        setNegativeButton(android.R.string.cancel, null)
    }
}