package com.zacharee1.systemuituner.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zacharee1.systemuituner.databinding.WriteSystemFailBinding
import com.zacharee1.systemuituner.util.launchUrl

class RecommendSystemAddOn : AppCompatActivity() {
    private val binding by lazy { WriteSystemFailBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        binding.getAddOn.setOnClickListener {
            launchUrl("https://zwander.dev/dialog-systemuitunersystemsettingsadd-on")
        }
    }
}