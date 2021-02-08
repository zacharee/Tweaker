package com.zacharee1.systemuituner.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.mikepenz.materialdrawer.util.ExperimentalNavController
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.databinding.ActivityDemoModeBinding
import com.zacharee1.systemuituner.dialogs.RoundedBottomSheetDialog
import com.zacharee1.systemuituner.fragments.intro.ExtraPermsSlide
import com.zacharee1.systemuituner.util.addAnimation
import com.zacharee1.systemuituner.util.hasDump

class DemoModeActivity : AppCompatActivity() {
    private val binding by lazy { ActivityDemoModeBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        binding.toolbar.addAnimation()

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (!hasDump) {
            ExtraPermsRetroactive.start(this, ExtraPermsSlide::class.java)
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    @ExperimentalNavController
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)

        val searchItem = menu.findItem(R.id.search)
        searchItem.isVisible = false

        val helpItem = menu.findItem(R.id.help)
        helpItem.isVisible = true
        helpItem.setOnMenuItemClickListener {
            RoundedBottomSheetDialog(this).apply {
                setTitle(R.string.help)
                setMessage(R.string.sub_demo_desc)
                setPositiveButton(android.R.string.ok, null)
                show()
            }
            false
        }

        return true
    }
}