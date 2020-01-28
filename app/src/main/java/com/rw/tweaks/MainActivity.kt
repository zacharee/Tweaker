package com.rw.tweaks

import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.holder.DimenHolder
import com.mikepenz.materialdrawer.model.*
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import com.mikepenz.materialdrawer.util.ExperimentalNavController
import com.mikepenz.materialdrawer.util.setupWithNavController
import com.rw.tweaks.activities.Intro
import com.rw.tweaks.dialogs.RoundedBottomSheetDialog
import com.rw.tweaks.drawer.IndentedSecondaryDrawerItem
import com.rw.tweaks.fragments.BasePrefFragment
import com.rw.tweaks.fragments.SearchFragment
import com.rw.tweaks.util.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*

@ExperimentalNavController
class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {
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
                    .withName(R.string.tweaks)
                    .withTextColor(getColor(R.color.colorAccent)),
                NavigationDrawerItem(
                    R.id.homeFragment,
                    PrimaryDrawerItem()
                        .withName(R.string.home)
                        .withIcon(R.drawable.ic_baseline_home_24)
                        .withIdentifier(R.id.homeFragment.toLong())
                ),
                NavigationDrawerItem(
                    R.id.appsFragment,
                    PrimaryDrawerItem()
                        .withName(R.string.category_apps)
                        .withIcon(R.drawable.ic_baseline_apps_24)
                        .withIdentifier(R.id.appsFragment.toLong())
                ),
                NavigationDrawerItem(
                    R.id.audioFragment,
                    PrimaryDrawerItem()
                        .withName(R.string.category_audio)
                        .withIcon(R.drawable.ic_baseline_volume_up_24)
                        .withIdentifier(R.id.audioFragment.toLong())
                ),
                NavigationDrawerItem(
                    R.id.developerFragment,
                    PrimaryDrawerItem()
                        .withName(R.string.category_developer)
                        .withIcon(R.drawable.ic_baseline_developer_mode_24)
                        .withIdentifier(R.id.developerFragment.toLong())
                ),
                NavigationDrawerItem(
                    R.id.displayFragment,
                    PrimaryDrawerItem()
                        .withName(R.string.category_display)
                        .withIcon(R.drawable.ic_baseline_tv_24)
                        .withIdentifier(R.id.displayFragment.toLong())
                ),
//                NavigationDrawerItem(
//                    0,
//                    PrimaryDrawerItem()
//                        .withName(R.string.category_easter_eggs)
//                        .withId(0L)
//                ),
                ExpandableDrawerItem()
                    .withName(R.string.category_network)
                    .withSelectable(false)
                    .withIdentifier(R.string.category_network.toLong())
                    .withIcon(R.drawable.ic_network)
                    .withSubItems(
                        NavigationDrawerItem(
                            R.id.netCellFragment,
                            IndentedSecondaryDrawerItem()
                                .withName(R.string.sub_cellular)
                                .withIcon(R.drawable.ic_baseline_signal_cellular_4_bar_24)
                                .withIdentifier(R.id.netCellFragment.toLong())
                        ),
                        NavigationDrawerItem(
                            R.id.netWiFiFragment,
                            IndentedSecondaryDrawerItem()
                                .withName(R.string.sub_wifi)
                                .withIcon(R.drawable.ic_baseline_signal_wifi_4_bar_24)
                                .withIdentifier(R.id.netWiFiFragment.toLong())
                        ),
                        NavigationDrawerItem(
                            R.id.netMiscellaneousFragment,
                            IndentedSecondaryDrawerItem()
                                .withName(R.string.sub_miscellaneous)
                                .withIcon(R.drawable.ic_baseline_more_horiz_24)
                                .withIdentifier(R.id.netMiscellaneousFragment.toLong())
                        )
                    ),
                NavigationDrawerItem(
                    R.id.notificationsFragment,
                    PrimaryDrawerItem()
                        .withName(R.string.category_notifications)
                        .withIcon(R.drawable.ic_baseline_notifications_24)
                        .withIdentifier(R.id.notificationsFragment.toLong())
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
                    .withIcon(R.drawable.ic_baseline_build_24)
                    .withIdentifier(R.string.category_system.toLong())
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
                                .withIcon(R.drawable.ic_baseline_sd_storage_24)
                                .withIdentifier(R.id.storageFragment.toLong())
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
                        .withIcon(R.drawable.ic_baseline_touch_app_24)
                        .withIdentifier(R.id.UIFragment.toLong())
                ),
                DividerDrawerItem(),
                SectionDrawerItem()
                    .withDivider(false)
                    .withName(R.string.more)
                    .withTextColor(getColor(R.color.colorAccent)),
                NavigationDrawerItem(
                    R.id.persistentActivity,
                    PrimaryDrawerItem()
                        .withName(R.string.screen_persistent)
                        .withIcon(R.drawable.ic_baseline_save_24)
                        .withSelectable(false)
                        .withIdentifier(R.id.persistentActivity.toLong())
                )
            )
            .apply {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
                    addDrawerItems(
                        PrimaryDrawerItem()
                            .withName(R.string.reset)
                            .withIcon(R.drawable.ic_baseline_restore_24)
                            .withSelectable(false)
                            .withOnDrawerItemClickListener(object : Drawer.OnDrawerItemClickListener {
                                override fun onItemClick(
                                    view: View?,
                                    position: Int,
                                    drawerItem: IDrawerItem<*>
                                ): Boolean {
                                    val dialog = RoundedBottomSheetDialog(this@MainActivity)
                                    dialog.setTitle(R.string.reset)
                                    dialog.setMessage(resources.getString(R.string.reset_confirm, buildNonResettablePreferences().joinToString(prefix = "\n- ", separator = "\n- ")))
                                    dialog.setPositiveButton(R.string.reset, DialogInterface.OnClickListener { _, _ ->
                                        resetAll()
                                    })
                                    dialog.setNegativeButton(android.R.string.cancel, null)

                                    dialog.show()

                                    return true
                                }

                            })
                    )
                }
            }
            .build()
    }

    private val searchFragment by lazy { search_fragment as SearchFragment }
    private val titleSwitcher by lazy { toolbar.screen_title }

    private val navController: NavController
        get() = findNavController(R.id.nav_host_fragment)

    private var searchView: SearchView? = null

    @ExperimentalNavController
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)

        if (!hasWss)
            Intro.start(this)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        toolbar.addAnimation()

        titleSwitcher.inAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_in)
        titleSwitcher.outAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_out)

        drawer.setupWithNavController(navController)
        drawer.recyclerView.setBackgroundColor(getColor(R.color.toolbarColor))
        navController.addOnDestinationChangedListener(this)

        searchFragment.onItemClickListener = { action, key ->
            navController.navigate(
                action,
                bundleOf(BasePrefFragment.ARG_HIGHLIGHT_KEY to key)
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
        searchView?.addAnimation()

        searchView?.setOnSearchClickListener {
            search_holder.apply {
                visibility = View.VISIBLE
                animate()
                    .alpha(1f)
            }
            searchView?.setOnQueryTextListener(searchFragment)

            titleSwitcher.isVisible = false
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

            titleSwitcher.scaleAnimatedVisible = true
            false
        }

        return true
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        val item = findDrawerItemByDestinationId(destination.id) ?: return
        drawer.setSelection(item, false)
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

    private fun findDrawerItemByDestinationId(id: Int): IDrawerItem<*>? {
//        return drawer.getDrawerItem(id.toLong())
        drawer.drawerItems.forEach {
            if (it is NavigationDrawerItem && it.resId == id) return it
            it.subItems.forEach { sub ->
                if (sub is NavigationDrawerItem && sub.resId == id) return sub
            }
        }

        return null
    }
}
