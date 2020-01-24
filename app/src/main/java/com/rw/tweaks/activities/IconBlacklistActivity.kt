package com.rw.tweaks.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.mikepenz.materialdrawer.util.ExperimentalNavController
import com.rw.tweaks.R
import com.rw.tweaks.dialogs.AnimatedMaterialAlertDialogBuilder
import com.rw.tweaks.fragments.IconBlacklistFragment
import com.rw.tweaks.util.addAnimation
import kotlinx.android.synthetic.main.activity_icon_blacklist.*
import kotlinx.android.synthetic.main.activity_persistent.toolbar

class IconBlacklistActivity : AppCompatActivity() {
    private val blacklistFragment by lazy { blacklist_fragment as IconBlacklistFragment }
    private var searchView: SearchView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_icon_blacklist)
        setSupportActionBar(toolbar)
        toolbar.addAnimation()

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

    @ExperimentalNavController
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
            AnimatedMaterialAlertDialogBuilder(this)
                .setTitle(R.string.help)
                .setMessage(R.string.special_sub_icon_blacklist_desc)
                .setPositiveButton(android.R.string.ok, null)
                .show()
            false
        }

        return true
    }
}