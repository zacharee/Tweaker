package com.rw.tweaks

import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.holder.DimenHolder
import com.mikepenz.materialdrawer.model.*
import com.mikepenz.materialdrawer.util.ExperimentalNavController
import com.mikepenz.materialdrawer.util.setupWithNavController
import com.rw.tweaks.activities.Intro
import com.rw.tweaks.drawer.IndentedSecondaryDrawerItem
import com.rw.tweaks.fragments.BasePrefFragment
import com.rw.tweaks.fragments.SearchFragment
import com.rw.tweaks.util.hasWss
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    @ExperimentalNavController
    private val drawer by lazy {
        DrawerBuilder().withActivity(this)
            .withToolbar(toolbar)
            .withHeader(R.layout.drawer_header)
            .withHeaderPadding(true)
            .withHeaderHeight(DimenHolder.fromDp(172))
            .addDrawerItems(
                SectionDrawerItem()
                    .withDivider(false)
                    .withName(R.string.tweaks),
                NavigationDrawerItem(
                    R.id.homeFragment,
                    PrimaryDrawerItem()
                        .withName(R.string.home)
                ),
                NavigationDrawerItem(
                    R.id.appsFragment,
                    PrimaryDrawerItem()
                        .withName(R.string.category_apps)
                ),
                NavigationDrawerItem(
                    R.id.audioFragment,
                    PrimaryDrawerItem()
                        .withName(R.string.category_audio)
                ),
                NavigationDrawerItem(
                    R.id.developerFragment,
                    PrimaryDrawerItem()
                        .withName(R.string.category_developer)
                ),
                NavigationDrawerItem(
                    R.id.displayFragment,
                    PrimaryDrawerItem()
                        .withName(R.string.category_display)
                ),
//                NavigationDrawerItem(
//                    0,
//                    PrimaryDrawerItem()
//                        .withName(R.string.category_easter_eggs)
//                ),
                ExpandableDrawerItem()
                    .withName(R.string.category_network)
                    .withSelectable(false)
                    .withSubItems(
                        NavigationDrawerItem(
                            R.id.netCellFragment,
                            IndentedSecondaryDrawerItem()
                                .withName(R.string.sub_cellular)
                        ),
                        NavigationDrawerItem(
                            R.id.netWiFiFragment,
                            IndentedSecondaryDrawerItem()
                                .withName(R.string.sub_wifi)
                        ),
                        NavigationDrawerItem(
                            R.id.netMiscellaneousFragment,
                            IndentedSecondaryDrawerItem()
                                .withName(R.string.sub_miscellaneous)
                        )
                    ),
                NavigationDrawerItem(
                    R.id.notificationsFragment,
                    PrimaryDrawerItem()
                        .withName(R.string.category_notifications)
                ),
//                ExpandableDrawerItem()
//                    .withSelectable(false)
//                    .withName(R.string.category_apps)
//                    .withSubItems(
//                        //TODO: Fill in
//                    ),
                ExpandableDrawerItem()
                    .withSelectable(false)
                    .withName(R.string.category_system)
                    .withSubItems(
//                        NavigationDrawerItem(
//                            0,
//                            IndentedSecondaryDrawerItem()
//                                .withName(R.string.sub_security)
//                        ),
                        NavigationDrawerItem(
                            R.id.storageFragment,
                            IndentedSecondaryDrawerItem()
                                .withName(R.string.sub_storage)
                        )
//                        NavigationDrawerItem(
//                            0,
//                            IndentedSecondaryDrawerItem()
//                                .withName(R.string.sub_power)
//                        ),
//                        NavigationDrawerItem(
//                            0,
//                            IndentedSecondaryDrawerItem()
//                                .withName(R.string.sub_miscellaneous)
//                        )
                    ),
                NavigationDrawerItem(
                    R.id.UIFragment,
                    PrimaryDrawerItem()
                        .withName(R.string.category_ui)
                ),
                DividerDrawerItem(),
                SectionDrawerItem()
                    .withDivider(false)
                    .withName(R.string.more),
                NavigationDrawerItem(
                    R.id.persistentActivity,
                    PrimaryDrawerItem()
                        .withName(R.string.screen_persistent)
                        .withSelectable(false)
                )
            )
            .build()
    }

    private val searchFragment by lazy { search_fragment as SearchFragment }

    private val navController: NavController
        get() = findNavController(R.id.nav_host_fragment)

    private var searchView: SearchView? = null

    @ExperimentalNavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!hasWss) Intro.start(this)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        drawer.setupWithNavController(navController)

        searchFragment.onItemClickListener = { action, key ->
            navController.navigate(
                action,
                Bundle().apply {
                    putString(BasePrefFragment.ARG_HIGHLIGHT_KEY, key)
                }
            )
            searchView?.setQuery("", false)
            searchView?.isIconified = true
        }
    }

    @ExperimentalNavController
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)

        val searchItem = menu.findItem(R.id.search)
        searchView = searchItem?.actionView as SearchView?

        searchView?.setOnSearchClickListener {
            search_holder.apply {
                visibility = View.VISIBLE
                animate()
                    .alpha(1f)
            }
            searchView?.setOnQueryTextListener(searchFragment)
        }

        searchView?.setOnCloseListener {
            search_holder.apply {
                animate()
                    .alpha(0f)
                    .withEndAction {
                        visibility = View.GONE
                    }
            }
            searchView?.setOnQueryTextListener(null)
            false
        }

        return true
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
}
