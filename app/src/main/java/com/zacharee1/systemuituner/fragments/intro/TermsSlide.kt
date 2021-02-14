package com.zacharee1.systemuituner.fragments.intro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.heinrichreimersoftware.materialintro.app.SlideFragment
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.databinding.TermsSlideBinding

class TermsSlide : SlideFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.terms_slide, container, false)
    }

    override fun canGoForward(): Boolean {
        return if (view == null) {
            false
        } else {
            val binding = TermsSlideBinding.bind(requireView())
            binding.termsRoot.canGoForward()
        }
    }
}