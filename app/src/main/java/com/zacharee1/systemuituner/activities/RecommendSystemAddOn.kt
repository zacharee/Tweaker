package com.zacharee1.systemuituner.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.util.launchUrl
import kotlinx.android.synthetic.main.write_system_fail.*

class RecommendSystemAddOn : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.write_system_fail)

        get_add_on.setOnClickListener {
            launchUrl("https://labs.xda-developers.com/store/app/tk.zwander.systemuituner.systemsettings")
        }
    }
}