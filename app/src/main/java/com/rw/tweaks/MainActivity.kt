package com.rw.tweaks

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.holder.DimenHolder
import com.mikepenz.materialdrawer.model.ExpandableDrawerItem
import com.mikepenz.materialdrawer.model.NavigationDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.util.ExperimentalNavController
import com.mikepenz.materialdrawer.util.setupWithNavController
import com.rw.tweaks.util.IndentedSecondaryDrawerItem
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    @ExperimentalNavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        DrawerBuilder().withActivity(this)
            .withToolbar(toolbar)
            .withHeader(R.layout.drawer_header)
            .withHeaderPadding(true)
            .withHeaderHeight(DimenHolder.fromDp(172))
            .addDrawerItems(
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
                    0,
                    PrimaryDrawerItem()
                        .withName(R.string.category_audio)
                ),
                NavigationDrawerItem(
                    0,
                    PrimaryDrawerItem()
                        .withName(R.string.category_developer)
                ),
                NavigationDrawerItem(
                    R.id.displayFragment,
                    PrimaryDrawerItem()
                        .withName(R.string.category_display)
                ),
                NavigationDrawerItem(
                    0,
                    PrimaryDrawerItem()
                        .withName(R.string.category_easter_eggs)
                ),
                ExpandableDrawerItem()
                    .withName(R.string.category_network)
                    .withSelectable(false)
                    .withSubItems(
                        NavigationDrawerItem(
                            0,
                            IndentedSecondaryDrawerItem()
                                .withName(R.string.sub_cellular)
                        ),
                        NavigationDrawerItem(
                            0,
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
                ExpandableDrawerItem()
                    .withSelectable(false)
                    .withName(R.string.category_apps)
                    .withSubItems(
                        //TODO: Fill in
                    ),
                ExpandableDrawerItem()
                    .withSelectable(false)
                    .withName(R.string.category_system)
                    .withSubItems(
                        NavigationDrawerItem(
                            0,
                            IndentedSecondaryDrawerItem()
                                .withName(R.string.sub_security)
                        ),
                        NavigationDrawerItem(
                            R.id.storageFragment,
                            IndentedSecondaryDrawerItem()
                                .withName(R.string.sub_storage)
                        ),
                        NavigationDrawerItem(
                            0,
                            IndentedSecondaryDrawerItem()
                                .withName(R.string.sub_power)
                        ),
                        NavigationDrawerItem(
                            0,
                            IndentedSecondaryDrawerItem()
                                .withName(R.string.sub_miscellaneous)
                        )
                    ),
                NavigationDrawerItem(
                    R.id.UIFragment,
                    PrimaryDrawerItem()
                        .withName(R.string.category_ui)
                )
            )
            .build()
            .setupWithNavController(findNavController(R.id.nav_host_fragment))
    }
}
