package com.zacharee1.systemuituner.dialogs

import android.content.Context
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.util.launchUrl

class PatreonDialog(context: Context) : ScrolledRoundedBottomSheetDialog(context) {
    init {
        setTitle(R.string.patreon)
        setMessage(R.string.patreon_desc)

        setPositiveButton(R.string.check_it_out) { _, _ ->
            context.launchUrl("https://patreon.com/zacharywander")
        }

        setNegativeButton(android.R.string.cancel, null)
    }
}