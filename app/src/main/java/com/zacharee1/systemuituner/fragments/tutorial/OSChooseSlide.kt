package com.zacharee1.systemuituner.fragments.tutorial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.heinrichreimersoftware.materialintro.app.SlideFragment
import com.zacharee1.systemuituner.IOSSelectionCallback
import com.zacharee1.systemuituner.R
import kotlinx.android.synthetic.main.choose_os_slide.view.choose_os

class OSChooseSlide : SlideFragment() {
    companion object {
        const val ARG_CALLBACK = "callback"

        fun newInstance(callback: IOSSelectionCallback): OSChooseSlide {
            val instance = OSChooseSlide()
            instance.arguments = Bundle().apply {
                putBinder(ARG_CALLBACK, callback.asBinder())
            }

            return instance
        }
    }

    private val callback by lazy { IOSSelectionCallback.Stub.asInterface(requireArguments().getBinder(ARG_CALLBACK)) }

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
                    callback?.onSelected(checkedId)
                }
            }
        }
    }

    override fun canGoForward(): Boolean {
        return view?.choose_os?.checkedRadioButtonId != -1
    }
}