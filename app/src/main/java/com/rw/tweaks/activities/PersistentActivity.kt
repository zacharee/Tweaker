package com.rw.tweaks.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import com.mikepenz.materialdrawer.util.ExperimentalNavController
import com.rw.tweaks.R
import com.rw.tweaks.fragments.PersistentFragment
import kotlinx.android.synthetic.main.activity_persistent.*

class PersistentActivity : AppCompatActivity() {
    private val persistentFragment by lazy { persistent_fragment as PersistentFragment }
    private var searchView: SearchView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_persistent)
        setSupportActionBar(toolbar)

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

        searchView?.setOnQueryTextListener(persistentFragment)

        return true
    }
}