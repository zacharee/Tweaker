package com.zacharee1.systemuituner.compose.preferences.layouts

import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zacharee1.systemuituner.IImmersiveSelectionCallback
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.activities.ImmersiveListSelector
import com.zacharee1.systemuituner.compose.rememberSettingsState
import com.zacharee1.systemuituner.data.SettingsType
import com.zacharee1.systemuituner.util.ImmersiveManager
import com.zacharee1.systemuituner.util.prefManager
import com.zacharee1.systemuituner.views.ImmersiveMode

private class ImmersiveSelectionCallbackWrapper(private val callback: (checked: List<String>) -> Unit) : IImmersiveSelectionCallback.Stub() {
    override fun onImmersiveResult(checked: MutableList<Any?>) {
        callback(checked.map { it.toString() })
    }
}

@Composable
fun ImmersiveModeLayout() {
    val context = LocalContext.current
    val immersiveManager = remember {
        ImmersiveManager(context = context)
    }

    var state by context.rememberSettingsState(
        key = SettingsType.GLOBAL to Settings.Global.POLICY_CONTROL,
        value = {
            immersiveManager.parseAdvancedImmersive()
        },
        writer = { immersiveManager.setAdvancedImmersive(it) },
        saveOption = true,
    )

    val items = remember {
        listOf(
            ImmersiveMode.ItemInfo(
                R.string.immersive_full,
                ImmersiveManager.ImmersiveMode.FULL
            ),
            ImmersiveMode.ItemInfo(
                R.string.immersive_status,
                ImmersiveManager.ImmersiveMode.STATUS
            ),
            ImmersiveMode.ItemInfo(
                R.string.immersive_nav,
                ImmersiveManager.ImmersiveMode.NAV
            )
        )
    }

    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(id = R.string.mode),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Start,
                )

                Text(
                    text = stringResource(id = R.string.all_apps),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.width(75.dp),
                    textAlign = TextAlign.Center,
                )

                Text(
                    text = stringResource(id = R.string.select_apps),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.width(75.dp),
                    textAlign = TextAlign.Center,
                )

                Text(
                    text = stringResource(id = R.string.immersive_blacklist),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.width(75.dp),
                    textAlign = TextAlign.Center,
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(items, { it.name }) { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        val all = when (item.type) {
                            ImmersiveManager.ImmersiveMode.FULL -> state.allFull
                            ImmersiveManager.ImmersiveMode.STATUS -> state.allStatus
                            ImmersiveManager.ImmersiveMode.NAV -> state.allNav
                            else -> false
                        }

                        Text(
                            text = stringResource(id = item.name),
                            modifier = Modifier.weight(1f)
                        )

                        Checkbox(
                            checked = all,
                            onCheckedChange = {
                                val newState = state.copy(
                                    allFull = if (item.type == ImmersiveManager.ImmersiveMode.FULL) it else state.allFull,
                                    allStatus = if (item.type == ImmersiveManager.ImmersiveMode.STATUS) it else state.allStatus,
                                    allNav = if (item.type == ImmersiveManager.ImmersiveMode.NAV) it else state.allNav,
                                )

                                state = newState
                            },
                            modifier = Modifier.width(75.dp),
                        )

                        IconButton(
                            onClick = {
                                ImmersiveListSelector.start(context, context.prefManager.getImmersiveWhitelist(item.type), ImmersiveSelectionCallbackWrapper {
                                    context.prefManager.putImmersiveWhitelist(item.type, it)
                                    state = state.copy(
                                        fullApps = if (item.type == ImmersiveManager.ImmersiveMode.FULL) ArrayList(it) else state.fullApps,
                                        statusApps = if (item.type == ImmersiveManager.ImmersiveMode.STATUS) ArrayList(it) else state.statusApps,
                                        navApps = if (item.type == ImmersiveManager.ImmersiveMode.NAV) ArrayList(it) else state.navApps,
                                    )
                                })
                            },
                            enabled = !all,
                            modifier = Modifier.width(75.dp),
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_baseline_playlist_add_24),
                                contentDescription = stringResource(id = R.string.immersive_whitelist)
                            )
                        }

                        IconButton(
                            onClick = {
                                ImmersiveListSelector.start(context, context.prefManager.getImmersiveBlacklist(item.type), ImmersiveSelectionCallbackWrapper {
                                    context.prefManager.putImmersiveBlacklist(item.type, it)
                                    state = state.copy(
                                        fullBl = if (item.type == ImmersiveManager.ImmersiveMode.FULL) ArrayList(it) else state.fullBl,
                                        statusBl = if (item.type == ImmersiveManager.ImmersiveMode.STATUS) ArrayList(it) else state.statusBl,
                                        navBl = if (item.type == ImmersiveManager.ImmersiveMode.NAV) ArrayList(it) else state.navBl,
                                    )
                                })
                            },
                            modifier = Modifier.width(75.dp),
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_playlist_remove_24dp),
                                contentDescription = stringResource(id = R.string.immersive_blacklist)
                            )
                        }
                    }
                }
            }
        }
    }
}
