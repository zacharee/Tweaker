package com.zacharee1.systemuituner.dialogs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zacharee1.systemuituner.R

open class ScrolledRoundedBottomSheetDialog(context: Context) : RoundedBottomSheetDialog(context) {
    init {
        super.setLayout(
            View.inflate(context, R.layout.base_message_pref_dialog_layout, null)
        )
    }

    override fun setLayout(layout: View) {
        findViewById<ViewGroup>(R.id.wrapper)!!.addView(layout)
    }
}