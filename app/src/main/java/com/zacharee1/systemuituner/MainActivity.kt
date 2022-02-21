package com.zacharee1.systemuituner

import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.zacharee1.systemuituner.activities.Intro
import com.zacharee1.systemuituner.databinding.ActivityMainBinding
import com.zacharee1.systemuituner.fragments.BasePrefFragment
import com.zacharee1.systemuituner.fragments.SearchFragment
import com.zacharee1.systemuituner.util.*

class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {
    private val mainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val searchFragment by lazy { supportFragmentManager.findFragmentById(R.id.search_fragment) as SearchFragment }
    private val titleSwitcher by lazy { mainBinding.screenTitle }

    private val navFragment by lazy { supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment }
    private val navController: NavController
        get() = navFragment.navController

    private var searchView: SearchView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)

        if (!hasWss)
            Intro.start(this)

        setContentView(mainBinding.root)
        setSupportActionBar(mainBinding.toolbar)

        with(supportActionBar) {
            this ?: return@with

            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
            setHomeAsUpIndicator(DrawerArrowDrawable(this@MainActivity))
        }

        mainBinding.toolbar.addAnimation()

        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        with(titleSwitcher) {
            inAnimation = AnimationUtils.loadAnimation(this@MainActivity, R.anim.scale_in)
            outAnimation = AnimationUtils.loadAnimation(this@MainActivity, R.anim.scale_out)
        }

        mainBinding.searchHolder.apply {
            translationX = width.toFloat()
        }

        navController.addOnDestinationChangedListener(this)

        mainBinding.root.closePane()

        searchFragment.onItemClickListener = { action, key ->
            navController.navigate(
                action,
                bundleOf(BasePrefFragment.ARG_HIGHLIGHT_KEY to key)
            )
            searchView?.setQuery("", false)
            searchView?.isIconified = true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            mainBinding.root.closePane()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)

        val searchItem = menu.findItem(R.id.search)
        searchView = searchItem?.actionView as SearchView?
        searchView?.addAnimation()

        with(searchView) {
            this ?: return@with

            setOnSearchClickListener {
                mainBinding.searchHolder.apply {
                    searchFragment.onShow()
                    visibility = View.VISIBLE
                    animate()
                        .alpha(1f)
                        .translationX(0f)
                        .withEndAction {
                        }
                }
                setOnQueryTextListener(searchFragment)

                titleSwitcher.isVisible = false
            }

            setOnCloseListener {
                closeSearch()
                false
            }
        }

        return true
    }

    private fun closeSearch() {
        mainBinding.searchHolder.apply {
            animate()
                .alpha(0f)
                .translationX(width.toFloat())
                .withEndAction {
                    visibility = View.GONE
                }
        }
        searchView?.setOnQueryTextListener(null)

        titleSwitcher.scaleAnimatedVisible = true
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        mainBinding.root.openPane()
    }

    override fun onBackPressed() {
        when {
            searchView?.isIconified == false -> {
                searchView?.isIconified = true
            }
            navController.currentDestination?.id != R.id.homeFragment -> {
                navController.navigate(
                    R.id.homeFragment, null,
                    NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .setEnterAnim(android.R.animator.fade_in)
                        .setExitAnim(android.R.animator.fade_out)
                        .setPopEnterAnim(android.R.animator.fade_in)
                        .setPopExitAnim(android.R.animator.fade_out)
                        .build()
                )
            }
            else -> {
                finish()
            }
        }
    }

    override fun setTitle(title: CharSequence?) {
        super.setTitle(null)

        titleSwitcher.setText(title)
    }
}
