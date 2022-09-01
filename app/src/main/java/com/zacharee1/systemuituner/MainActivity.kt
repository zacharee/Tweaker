package com.zacharee1.systemuituner

import android.animation.ValueAnimator
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.view.animation.AccelerateInterpolator
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.core.os.bundleOf
import androidx.core.view.updateLayoutParams
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import com.zacharee1.systemuituner.activities.Intro
import com.zacharee1.systemuituner.databinding.ActivityMainBinding
import com.zacharee1.systemuituner.fragments.BasePrefFragment
import com.zacharee1.systemuituner.fragments.HomeFragment
import com.zacharee1.systemuituner.fragments.SearchFragment
import com.zacharee1.systemuituner.util.*

class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {
    private val mainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val searchFragment by lazy { supportFragmentManager.findFragmentById(R.id.search_fragment) as SearchFragment }
    private val homeFragment by lazy { supportFragmentManager.findFragmentById(R.id.nav_home_fragment) as HomeFragment }
    private val titleSwitcher by lazy { mainBinding.screenTitle }

    private val navFragment by lazy { supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment }
    private val navController: NavController
        get() = navFragment.navController

    private val searchView by lazy { mainBinding.searchBar }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)

        when {
            !hasWss -> {
                Intro.start(this)
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                    !Settings.canDrawOverlays(this) &&
                    !prefManager.sawSystemAlertWindow -> {
                Intro.start(this, Intro.Companion.StartReason.SYSTEM_ALERT_WINDOW)
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                    checkCallingOrSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED &&
                    !prefManager.sawNotificationsAlert -> {
                Intro.start(this, Intro.Companion.StartReason.NOTIFICATIONS)
            }
        }

        setContentView(mainBinding.root)
        setSupportActionBar(mainBinding.toolbar)

        with(supportActionBar) {
            this ?: return@with

            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
            setHomeAsUpIndicator(DrawerArrowDrawable(this@MainActivity))
        }

        mainBinding.toolbar.addAnimation()

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
            searchView.setQuery("", false)
            closeSearch()
        }

        homeFragment.onSearchClickListener = {
            searchFragment.onShow()
            mainBinding.searchHolder.apply {
                visibility = View.VISIBLE
                animate()
                    .alpha(1f)
                    .translationX(0f)
                    .withEndAction {
                        visibility = View.VISIBLE
                    }
            }
            searchView.setOnQueryTextListener(searchFragment)
            searchView.isIconified = false
        }

        searchView.addAnimation()
        searchView.setOnCloseListener {
            closeSearch()
            true
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        updateDrawerWidth(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            mainBinding.root.closePane()
            if (!mainBinding.root.isSlideable) {
                updateDrawerWidth()
            }
            return true
        }

        return super.onOptionsItemSelected(item)
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
        searchView.setQuery("", false)
        searchView.setOnQueryTextListener(null)
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
            !searchView.isIconified -> {
                closeSearch()
            }
            mainBinding.root.isOpen -> {
                mainBinding.root.closePane()
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

    private var drawerAnimator: ValueAnimator? = null

    private fun updateDrawerWidth(visible: Boolean = mainBinding.drawerLayout.width == 0) {
        drawerAnimator?.cancel()
        drawerAnimator = ValueAnimator.ofInt(
            mainBinding.drawerLayout.width,
            if (visible) resources.getDimensionPixelSize(R.dimen.drawer_width) else 0
        )
        drawerAnimator?.duration =
            resources.getInteger(android.R.integer.config_mediumAnimTime).toLong()
        drawerAnimator?.interpolator =
            if (visible) DecelerateInterpolator() else AccelerateInterpolator()

        drawerAnimator?.addUpdateListener {
            mainBinding.drawerLayout.updateLayoutParams<ViewGroup.LayoutParams> {
                width = it.animatedValue.toString().toInt()
            }
        }
        drawerAnimator?.start()
    }
}
