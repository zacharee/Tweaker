package com.zacharee1.systemuituner.fragments.intro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.databinding.ExtraPermsSlideBinding

class ExtraPermsSlide : PermGrantSlide() {
    override val permissions = arrayOf(
        android.Manifest.permission.PACKAGE_USAGE_STATS,
        android.Manifest.permission.DUMP
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.extra_perms_slide, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = ExtraPermsSlideBinding.bind(view)

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