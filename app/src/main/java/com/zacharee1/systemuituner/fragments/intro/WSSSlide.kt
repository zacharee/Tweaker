package com.zacharee1.systemuituner.fragments.intro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.databinding.WssSlideBinding

class WSSSlide : PermGrantSlide() {
    override val permissions = arrayOf(
        android.Manifest.permission.WRITE_SECURE_SETTINGS
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.wss_slide, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = WssSlideBinding.bind(view)

        view.apply {
            binding.grant.setOnClickListener {
                tryPermissionsGrant()
            }
            binding.help.setOnClickListener {
                startTutorialActivity()
            }
        }
    }
}