package com.zacharee1.systemuituner.fragments

import androidx.fragment.app.Fragment
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.util.updateTitle

class EmptyFragment : Fragment(R.layout.fragment_empty) {
    override fun onResume() {
        super.onResume()

        updateTitle(R.string.app_name)
    }
}