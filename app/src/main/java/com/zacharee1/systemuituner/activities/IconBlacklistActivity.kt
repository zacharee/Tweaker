package com.zacharee1.systemuituner.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.databinding.ActivityIconBlacklistBinding
import com.zacharee1.systemuituner.dialogs.RoundedBottomSheetDialog
import com.zacharee1.systemuituner.fragments.IconBlacklistFragment
import com.zacharee1.systemuituner.util.addAnimation

class IconBlacklistActivity : AppCompatActivity() {
    private val binding by lazy { ActivityIconBlacklistBinding.inflate(layoutInflater) }
    private val blacklistFragment by lazy { supportFragmentManager.findFragmentById(R.id.blacklist_fragment) as IconBlacklistFragment }
    private var searchView: SearchView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        binding.toolbar.addAnimation()

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)

        val searchItem = menu.findItem(R.id.search)
        searchView = searchItem?.actionView as SearchView?

        searchView?.setOnQueryTextListener(blacklistFragment)
        searchView?.setOnCloseListener(blacklistFragment)
        searchView?.addAnimation()

        val helpItem = menu.findItem(R.id.help)
        helpItem.isVisible = true
        helpItem.setOnMenuItemClickListener {
            RoundedBottomSheetDialog(this).apply {
                setTitle(R.string.help)
                setMessage(R.string.special_sub_icon_blacklist_desc)
                setPositiveButton(android.R.string.ok, null)
                show()
            }
            false
        }

        return true
    }
}