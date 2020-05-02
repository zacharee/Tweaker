package com.zacharee1.systemuituner

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.animation.AnimationUtils
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.customview.widget.ViewDragHelper
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.mikepenz.materialdrawer.holder.DimenHolder
import com.mikepenz.materialdrawer.holder.ImageHolder
import com.mikepenz.materialdrawer.holder.StringHolder
import com.mikepenz.materialdrawer.model.*
import com.mikepenz.materialdrawer.util.ExperimentalNavController
import com.mikepenz.materialdrawer.util.addItems
import com.mikepenz.materialdrawer.util.setupWithNavController
import com.zacharee1.systemuituner.activities.Intro
import com.zacharee1.systemuituner.dialogs.DonateDialog
import com.zacharee1.systemuituner.dialogs.RoundedBottomSheetDialog
import com.zacharee1.systemuituner.drawer.IndentedSecondaryDrawerItem
import com.zacharee1.systemuituner.fragments.BasePrefFragment
import com.zacharee1.systemuituner.fragments.SearchFragment
import com.zacharee1.systemuituner.util.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*

@ExperimentalNavController
class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {
    private val searchFragment by lazy { search_fragment as SearchFragment }
    private val titleSwitcher by lazy { toolbar.screen_title }

    private val navController: NavController
        get() = findNavController(R.id.nav_host_fragment)

    private val drawerToggle by lazy { ActionBarDrawerToggle(this, root, toolbar, R.string.material_drawer_open, R.string.material_drawer_close) }

    private var searchView: SearchView? = null

    @ExperimentalNavController
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)

        if (!hasWss)
            Intro.start(this)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        toolbar.addAnimation()

        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        setUpDrawer()
        root.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerClosed(drawerView: View) {
                updateDragEdgeSize()
            }

            override fun onDrawerOpened(drawerView: View) {
                updateDragEdgeSize()
            }

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}
            override fun onDrawerStateChanged(newState: Int) {}
        })

        titleSwitcher.inAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_in)
        titleSwitcher.outAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_out)

        slider.headerPadding = true
        slider.headerHeight = DimenHolder.fromDp(172)
        slider.headerView = LayoutInflater.from(this).inflate(R.layout.drawer_header, null)
        slider.setupWithNavController(navController)
        slider.recyclerView.setBackgroundColor(getColor(R.color.toolbarColor))

        updateDragEdgeSize()

        slider.drawerLayout?.addDrawerListener(drawerToggle)

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

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        drawerToggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        updateDragEdgeSize()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (drawerToggle.onOptionsItemSelected(item))
            return true

        return super.onOptionsItemSelected(item)
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
//        val item = findDrawerItemByDestinationId(destination.id) ?: return
        slider.setSelection(destination.id.toLong(), false)
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

    @SuppressLint("RestrictedApi")
    private fun updateDragEdgeSize() {
        root.also {
            it::class.java.apply {
                (getDeclaredField("mLeftDragger").apply { isAccessible = true }
                    .get(it) as ViewDragHelper).edgeSize = if (root.isOpen) 0 else dpAsPx(resources.configuration.screenWidthDp)
            }
        }
    }

    private fun setUpDrawer() {
        slider.addItems(
            SectionDrawerItem()
                .apply {
                    divider = false
                    name = StringHolder(R.string.tweaks)
                    textColor = ColorStateList.valueOf(getColor(R.color.colorAccent))
                },
            NavigationDrawerItem(
                R.id.homeFragment,
                PrimaryDrawerItem().apply {
                    name = StringHolder(R.string.home)
                    icon = ImageHolder(R.drawable.ic_baseline_home_24)
                    identifier = R.id.homeFragment.toLong()
                }
            ),
            NavigationDrawerItem(
                R.id.appsFragment,
                PrimaryDrawerItem().apply {
                    name = StringHolder(R.string.category_apps)
                    icon = ImageHolder(R.drawable.ic_baseline_apps_24)
                    identifier = R.id.appsFragment.toLong()
                }
            ),
            NavigationDrawerItem(
                R.id.audioFragment,
                PrimaryDrawerItem().apply {
                    name = StringHolder(R.string.category_audio)
                    icon = ImageHolder(R.drawable.ic_baseline_volume_up_24)
                    identifier = R.id.audioFragment.toLong()
                }
            ),
            NavigationDrawerItem(
                R.id.developerFragment,
                PrimaryDrawerItem().apply {
                    name = StringHolder(R.string.category_developer)
                    icon = ImageHolder(R.drawable.ic_baseline_developer_mode_24)
                    identifier = R.id.developerFragment.toLong()
                }
            ),
            NavigationDrawerItem(
                R.id.displayFragment,
                PrimaryDrawerItem().apply {
                    name = StringHolder(R.string.category_display)
                    icon = ImageHolder(R.drawable.ic_baseline_tv_24)
                    identifier = R.id.displayFragment.toLong()
                }
            ),
            ExpandableDrawerItem().apply {
                name = StringHolder(R.string.category_network)
                isSelectable = false
                identifier = R.string.category_network.toLong()
                icon = ImageHolder(R.drawable.ic_network)
                subItems = mutableListOf(
                    NavigationDrawerItem(
                        R.id.netCellFragment,
                        IndentedSecondaryDrawerItem().apply {
                            name = StringHolder(R.string.sub_cellular)
                            icon = ImageHolder(R.drawable.ic_baseline_signal_cellular_4_bar_24)
                            identifier = R.id.netCellFragment.toLong()
                        }
                    ),
                    NavigationDrawerItem(
                        R.id.netWiFiFragment,
                        IndentedSecondaryDrawerItem().apply {
                            name = StringHolder(R.string.sub_wifi)
                            icon = ImageHolder(R.drawable.ic_baseline_signal_wifi_4_bar_24)
                            identifier = R.id.netWiFiFragment.toLong()
                        }
                    ),
                    NavigationDrawerItem(
                        R.id.netMiscellaneousFragment,
                        IndentedSecondaryDrawerItem().apply {
                            name = StringHolder(R.string.sub_miscellaneous)
                            icon = ImageHolder(R.drawable.ic_baseline_more_horiz_24)
                            identifier = R.id.netMiscellaneousFragment.toLong()
                        }
                    )
                )
            },
            NavigationDrawerItem(
                R.id.notificationsFragment,
                PrimaryDrawerItem().apply {
                    name = StringHolder(R.string.category_notifications)
                    icon = ImageHolder(R.drawable.ic_baseline_notifications_24)
                    identifier = R.id.notificationsFragment.toLong()
                }
            ),
            ExpandableDrawerItem().apply {
                isSelectable = false
                name = StringHolder(R.string.category_system)
                icon = ImageHolder(R.drawable.ic_baseline_build_24)
                identifier = R.string.category_system.toLong()
                subItems = mutableListOf(
                    NavigationDrawerItem(
                        R.id.storageFragment,
                        IndentedSecondaryDrawerItem().apply {
                            name = StringHolder(R.string.sub_storage)
                            icon = ImageHolder(R.drawable.ic_baseline_sd_storage_24)
                            identifier = R.id.storageFragment.toLong()
                        }
                    )
                )
            },
            NavigationDrawerItem(
                R.id.UIFragment,
                PrimaryDrawerItem().apply {
                    name = StringHolder(R.string.category_ui)
                    icon = ImageHolder(R.drawable.ic_baseline_touch_app_24)
                    identifier = R.id.UIFragment.toLong()
                }
            ),
            NavigationDrawerItem(
                R.id.advancedFragment,
                PrimaryDrawerItem().apply {
                    name = StringHolder(R.string.advanced)
                    icon = ImageHolder(R.drawable.tools)
                    identifier = R.id.advancedFragment.toLong()
                }
            ),
            DividerDrawerItem(),
            SectionDrawerItem().apply {
                divider = false
                name = StringHolder(R.string.more)
                textColor = ColorStateList.valueOf(getColor(R.color.colorAccent))
            },
            NavigationDrawerItem(
                R.id.persistentActivity,
                PrimaryDrawerItem().apply {
                    name = StringHolder(R.string.screen_persistent)
                    icon = ImageHolder(R.drawable.ic_baseline_save_24)
                    isSelectable = false
                    identifier = R.id.persistentActivity.toLong()
                }
            )
        )

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
            slider.addItems(
                PrimaryDrawerItem().apply {
                    name = StringHolder(R.string.reset)
                    icon = ImageHolder(R.drawable.ic_baseline_restore_24)
                    isSelectable = false
                    onDrawerItemClickListener = { _, _, _ ->
                        val dialog = RoundedBottomSheetDialog(this@MainActivity)
                        dialog.setTitle(R.string.reset)
                        dialog.setMessage(resources.getString(R.string.reset_confirm, buildNonResettablePreferences().joinToString(prefix = "\n- ", separator = "\n- ")))
                        dialog.setPositiveButton(R.string.reset, DialogInterface.OnClickListener { _, _ ->
                            resetAll()
                            dialog.dismiss()
                        })
                        dialog.setNegativeButton(android.R.string.cancel, null)
                        dialog.show()

                        true
                    }
                }
            )
        }

        if (isTouchWiz) {
            slider.addItems(
                PrimaryDrawerItem().apply {
                    name = StringHolder(R.string.oneui_tuner)
                    icon = ImageHolder(R.drawable.ic_baseline_android_24)
                    isSelectable = false
                    onDrawerItemClickListener = { _, _, _ ->
                        val dialog = RoundedBottomSheetDialog(this@MainActivity)
                        dialog.setTitle(R.string.oneui_tuner)
                        dialog.setMessage(R.string.oneui_tuner_desc)
                        dialog.setPositiveButton(R.string.check_it_out, DialogInterface.OnClickListener {_, _ ->
                            launchUrl("https://labs.xda-developers.com/store/app/tk.zwander.oneuituner")
                            dialog.dismiss()
                        })
                        dialog.setNegativeButton(android.R.string.cancel, null)
                        dialog.show()

                        true
                    }
                }
            )
        }

        slider.addItems(
            DividerDrawerItem(),
            SectionDrawerItem().apply {
                divider = false
                name = StringHolder(R.string.social)
                textColor = ColorStateList.valueOf(getColor(R.color.colorAccent))
            },
            PrimaryDrawerItem().apply {
                name = StringHolder(R.string.donate)
                icon = ImageHolder(R.drawable.ic_baseline_attach_money_24)
                isSelectable = false
                onDrawerItemClickListener = { _, _, _ ->
                    DonateDialog(this@MainActivity)
                        .show()
                    true
                }
            },
            PrimaryDrawerItem().apply {
                name = StringHolder(R.string.twitter)
                icon = ImageHolder(R.drawable.twitter)
                isSelectable = false
                onDrawerItemClickListener = { _, _, _ ->
                    launchUrl("https://twitter.com/Wander1236")
                    true
                }
            },
            PrimaryDrawerItem().apply {
                name = StringHolder(R.string.telegram)
                icon = ImageHolder(R.drawable.telegram)
                isSelectable = false
                onDrawerItemClickListener = { _, _, _ ->
                    launchUrl("https://bit.ly/ZachareeTG")
                    true
                }
            },
            PrimaryDrawerItem().apply {
                name = StringHolder(R.string.email)
                icon = ImageHolder(R.drawable.ic_baseline_email_24)
                isSelectable = false
                onDrawerItemClickListener = { _, _, _ ->
                    launchEmail("zachary@zwander.dev", getString(R.string.app_name))
                    true
                }
            },
            PrimaryDrawerItem().apply {
                name = StringHolder(R.string.website)
                icon = ImageHolder(R.drawable.earth)
                isSelectable = false
                onDrawerItemClickListener = { _, _, _ ->
                    launchUrl("https://zwander.dev")
                    true
                }
            },
            PrimaryDrawerItem().apply {
                name = StringHolder(R.string.more_apps)
                icon = ImageHolder(R.drawable.ic_baseline_apps_24)
                isSelectable = false
                onDrawerItemClickListener = { _, _, _ ->
                    launchUrl("https://play.google.com/store/apps/dev?id=6168495537212917027")
                    true
                }
            }
        )
    }
}
