package com.rw.tweaks.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rw.tweaks.R
import com.rw.tweaks.fragments.intro.ExtraPermsSlide
import com.rw.tweaks.util.hasDump

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