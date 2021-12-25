package com.zacharee1.systemuituner

import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.mikepenz.materialdrawer.holder.DimenHolder
import com.mikepenz.materialdrawer.holder.ImageHolder
import com.mikepenz.materialdrawer.holder.StringHolder
import com.mikepenz.materialdrawer.model.*
import com.mikepenz.materialdrawer.util.addItems
import com.mikepenz.materialdrawer.util.setupWithNavController
import com.zacharee1.systemuituner.activities.Intro
import com.zacharee1.systemuituner.databinding.ActivityMainBinding
import com.zacharee1.systemuituner.dialogs.DonateDialog
import com.zacharee1.systemuituner.dialogs.PatreonDialog
import com.zacharee1.systemuituner.dialogs.RoundedBottomSheetDialog
import com.zacharee1.systemuituner.dialogs.SupportersDialog
import com.zacharee1.systemuituner.drawer.IndentedSecondaryDrawerItem
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
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(DrawerArrowDrawable(this))
        mainBinding.toolbar.addAnimation()

        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        setUpDrawer()

        titleSwitcher.inAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_in)
        titleSwitcher.outAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_out)

        mainBinding.root.openPane()

        mainBinding.slider.headerPadding = true
        mainBinding.slider.headerHeight = DimenHolder.fromDp(172)
        mainBinding.slider.headerView = LayoutInflater.from(this).inflate(R.layout.drawer_header, null)
        mainBinding.slider.setupWithNavController(navController)
        mainBinding.slider.recyclerView.setBackgroundColor(ContextCompat.getColor(this, R.color.toolbarColor))

        mainBinding.searchHolder.apply {
            translationX = width.toFloat()
        }

        val currentListener = mainBinding.slider.onDrawerItemClickListener
        mainBinding.slider.onDrawerItemClickListener = { view, item, position ->
            if (item.isSelectable && item is NavigationDrawerItem) {
                mainBinding.root.openPane()
//                mainBinding.slider.drawerLayout?.closeDrawer(mainBinding.slider)
            }
            currentListener?.invoke(view, item, position) ?: false
        }

        mainBinding.root.sliderFadeColor = ContextCompat.getColor(this, R.color.colorSliderFade)

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
//            mainBinding.slider.drawerLayout?.openDrawer(mainBinding.slider)
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

        searchView?.setOnSearchClickListener {
            mainBinding.searchHolder.apply {
                searchFragment.onShow()
                visibility = View.VISIBLE
                animate()
                    .alpha(1f)
                    .translationX(0f)
                    .withEndAction {
                    }
            }
            searchView?.setOnQueryTextListener(searchFragment)

            titleSwitcher.isVisible = false
        }

        searchView?.setOnCloseListener {
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
            false
        }

        return true
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        mainBinding.slider.setSelection(destination.id.toLong(), false)
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

    private fun setUpDrawer() {
        mainBinding.slider.addItems(
            SectionDrawerItem()
                .apply {
                    divider = false
                    name = StringHolder(R.string.tweaks)
                    textColor = ColorStateList.valueOf(ContextCompat
                        .getColor(this@MainActivity, R.color.colorAccent))
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
            ExpandableDrawerItem().apply {
                name = StringHolder(R.string.category_interaction)
                isSelectable = false
                identifier = R.string.category_interaction.toLong()
                icon = ImageHolder(R.drawable.ic_baseline_touch_app_24)
                subItems = mutableListOf(
                    NavigationDrawerItem(
                        R.id.notificationsFragment,
                        IndentedSecondaryDrawerItem().apply {
                            name = StringHolder(R.string.category_notifications)
                            icon = ImageHolder(R.drawable.ic_baseline_notifications_24)
                            identifier = R.id.notificationsFragment.toLong()
                        }
                    ),
                    NavigationDrawerItem(
                        R.id.statusBarFragment,
                        IndentedSecondaryDrawerItem().apply {
                            name = StringHolder(R.string.category_status_bar)
                            icon = ImageHolder(R.drawable.ic_baseline_space_bar_24)
                            identifier = R.id.statusBarFragment.toLong()
                        }
                    ),
                    NavigationDrawerItem(
                        R.id.qsFragment,
                        IndentedSecondaryDrawerItem().apply {
                            name = StringHolder(R.string.category_quick_settings)
                            icon = ImageHolder(R.drawable.ic_baseline_view_grid_24)
                            identifier = R.id.qsFragment.toLong()
                        }
                    )
                )
            },
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
                textColor = ColorStateList.valueOf(ContextCompat
                    .getColor(this@MainActivity, R.color.colorAccent))
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
            mainBinding.slider.addItems(
                PrimaryDrawerItem().apply {
                    name = StringHolder(R.string.reset)
                    icon = ImageHolder(R.drawable.ic_baseline_restore_24)
                    isSelectable = false
                    onDrawerItemClickListener = { _, _, _ ->
                        val dialog = RoundedBottomSheetDialog(this@MainActivity)
                        dialog.setTitle(R.string.reset)
                        dialog.setMessage(resources.getString(R.string.reset_confirm,
                            buildNonResettablePreferences().joinToString(prefix = "\n- ", separator = "\n- ")))
                        dialog.setPositiveButton(R.string.reset) { _, _ ->
                            resetAll()
                            dialog.dismiss()
                        }
                        dialog.setNegativeButton(android.R.string.cancel, null)
                        dialog.show()

                        true
                    }
                }
            )
        }

        if (isTouchWiz) {
            mainBinding.slider.addItems(
                PrimaryDrawerItem().apply {
                    name = StringHolder(R.string.oneui_tuner)
                    icon = ImageHolder(R.drawable.ic_baseline_android_24)
                    isSelectable = false
                    onDrawerItemClickListener = { _, _, _ ->
                        val dialog = RoundedBottomSheetDialog(this@MainActivity)
                        dialog.setTitle(R.string.oneui_tuner)
                        dialog.setMessage(R.string.oneui_tuner_desc)
                        dialog.setPositiveButton(R.string.check_it_out) { _, _ ->
                            launchUrl("https://zwander.dev/dialog-oneuituner")
                            dialog.dismiss()
                        }
                        dialog.setNegativeButton(android.R.string.cancel, null)
                        dialog.show()

                        true
                    }
                }
            )
        }

        mainBinding.slider.addItems(
            PrimaryDrawerItem().apply {
                name = StringHolder(R.string.help_translate)
                icon = ImageHolder(R.drawable.ic_baseline_translate_24)
                isSelectable = false
                onDrawerItemClickListener = { _, _, _ ->
                    launchUrl("https://crowdin.com/project/systemui-tuner")
                    true
                }
            },
            DividerDrawerItem(),
            SectionDrawerItem().apply {
                divider = false
                name = StringHolder(R.string.social)
                textColor = ColorStateList.valueOf(ContextCompat
                    .getColor(this@MainActivity, R.color.colorAccent))
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
                name = StringHolder(R.string.supporters)
                icon = ImageHolder(R.drawable.heart_outline)
                isSelectable = false
                onDrawerItemClickListener = { _, _, _ ->
                    SupportersDialog(this@MainActivity)
                        .show()
                    true
                }
            },
            PrimaryDrawerItem().apply {
                name = StringHolder(R.string.patreon)
                icon = ImageHolder(R.drawable.patreon)
                isSelectable = false
                onDrawerItemClickListener = { _, _, _ ->
                    PatreonDialog(this@MainActivity).show()
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
