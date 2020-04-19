package com.zacharee1.systemuituner.dialogs

import android.os.Bundle

class OptionDialog : BaseOptionDialog() {
    companion object {
        fun newInstance(key: String, layoutRes: Int): OptionDialog {
            val instance = OptionDialog()
            val args = Bundle()

            args.putInt(ARG_LAYOUT_RES, layoutRes)
            args.putString(ARG_KEY, key)

            instance.arguments = args

            return instance
        }
    }

}