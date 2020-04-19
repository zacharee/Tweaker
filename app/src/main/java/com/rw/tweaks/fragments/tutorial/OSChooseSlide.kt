package com.rw.tweaks.fragments.tutorial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.heinrichreimersoftware.materialintro.app.SlideFragment
import com.rw.tweaks.R
import kotlinx.android.synthetic.main.choose_os_slide.view.choose_os

class OSChooseSlide(private val selectionCallback: (which: Int) -> Unit) : SlideFragment() {
    private var previousSelected = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(requireContext()).inflate(R.layout.choose_os_slide, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.choose_os.setOnCheckedChangeListener { group, checkedId ->
            if (previousSelected != checkedId) {
                previousSelected = checkedId
                group.post {
                    selectionCallback(checkedId)
                }
            }
        }
    }

    override fun canGoForward(): Boolean {
        return view?.choose_os?.checkedRadioButtonId != -1
    }
}