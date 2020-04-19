package com.zacharee1.systemuituner.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.fragments.intro.ExtraPermsSlide
import com.zacharee1.systemuituner.util.hasDump

class DemoModeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_demo_mode)

        if (!hasDump) {
            ExtraPermsRetroactive.start(this, ExtraPermsSlide::class.java)
            finish()
        }
    }
}