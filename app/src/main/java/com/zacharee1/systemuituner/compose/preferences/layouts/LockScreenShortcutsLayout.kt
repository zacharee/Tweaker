package com.zacharee1.systemuituner.compose.preferences.layouts

import android.content.ComponentName
import android.content.pm.PackageManager
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.zacharee1.systemuituner.ILockscreenShortcutSelectedCallback
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.activities.LockscreenShortcutSelector
import com.zacharee1.systemuituner.compose.rememberSettingsState
import com.zacharee1.systemuituner.data.SettingsType
import com.zacharee1.systemuituner.util.buildDefaultSamsungLockScreenShortcuts
import com.zacharee1.systemuituner.util.getActivityInfoCompat
import com.zacharee1.systemuituner.util.getSetting
import com.zacharee1.systemuituner.util.isTouchWiz
import com.zacharee1.systemuituner.util.writeSetting
import com.zacharee1.systemuituner.views.LockscreenShortcuts
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LockScreenShortcutsLayout() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val items = remember {
        listOf(
            LockscreenShortcuts.ShortcutInfo(
                label = R.string.option_lockscreen_shortcut_left,
                key = if (context.isTouchWiz) "lock_application_shortcut" else "sysui_keyguard_left",
                side = LockscreenShortcuts.ShortcutInfo.Side.LEFT,
            ),
            LockscreenShortcuts.ShortcutInfo(
                label = R.string.option_lockscreen_shortcut_right,
                key = if (context.isTouchWiz) "lock_application_shortcut" else "sysui_keyguard_right",
                side = LockscreenShortcuts.ShortcutInfo.Side.RIGHT,
            ),
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(items, { it.label }) { item ->
            var cName by context.rememberSettingsState(
                keys = arrayOf(
                    SettingsType.SECURE to item.key,
                    SettingsType.SYSTEM to item.key,
                ),
                value = {
                    if (context.isTouchWiz) {
                        val values = LockscreenShortcuts.ShortcutInfo.ComponentValues
                            .fromString(context.getSetting(SettingsType.SYSTEM, item.key))

                        values.getForSide(item.side)
                    } else {
                        context.getSetting(SettingsType.SECURE, item.key)
                    }
                },
                saveOption = true,
                writer = {
                    if (context.isTouchWiz) {
                        val current = LockscreenShortcuts.ShortcutInfo.ComponentValues
                            .fromString(context.getSetting(SettingsType.SYSTEM, item.key))
                        current.setForSide(item.side, it)

                        val string = current.toSettingsString()

                        context.writeSetting(SettingsType.SYSTEM, item.key, string, saveOption = true)
                    } else {
                        context.writeSetting(SettingsType.SECURE, item.key, it, saveOption = true)
                    }
                }
            )

            val icon = remember(cName) {
                val component = cName
                if (component != null) {
                    if (component.contains("NoUnlockNeeded/") && !component.contains(".")) {
                        when {
                            component.contains("Flashlight") -> ContextCompat.getDrawable(context, R.drawable.baseline_flashlight_on_24)
                            component.contains("Dnd") -> ContextCompat.getDrawable(context, R.drawable.do_not_disturb)
                            else -> null
                        }
                    } else {
                        try {
                            context.packageManager.getApplicationIcon(ComponentName.unflattenFromString(component)?.packageName)
                        } catch (e: PackageManager.NameNotFoundException) {
                            null
                        }
                    }
                } else {
                    null
                }
            }

            val label = remember(cName) {
                val component = cName
                if (component != null) {
                    if (component.contains("NoUnlockNeeded/") && !component.contains(".")) {
                        when {
                            component.contains("Flashlight") -> context.resources.getString(R.string.flashlight)
                            component.contains("Dnd") -> context.resources.getString(R.string.icon_blacklist_do_not_disturb)
                            else -> null
                        }
                    } else {
                        ComponentName.unflattenFromString(component)?.let {
                            try {
                                context.packageManager.getActivityInfoCompat(it)
                                    .loadLabel(context.packageManager)
                            } catch (e: PackageManager.NameNotFoundException) {
                                null
                            }
                        }
                    }
                } else {
                    null
                }
            }

            OutlinedCard(
                onClick = {
                    LockscreenShortcutSelector.start(context, item.key, object : ILockscreenShortcutSelectedCallback.Stub() {
                        override fun onSelected(component: String?, key: String) {
                            scope.launch {
                                cName = component
                            }
                        }
                    })
                },
                modifier = Modifier.fillMaxWidth()
                    .animateContentSize(),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Image(
                        painter = rememberDrawablePainter(drawable = icon),
                        contentDescription = label?.toString(),
                        modifier = Modifier.size(48.dp),
                    )

                    Column(
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(
                            text = stringResource(id = item.label),
                            style = MaterialTheme.typography.titleMedium,
                        )

                        label?.let {
                            Text(
                                text = label.toString(),
                            )
                        }

                        cName?.let {
                            Text(
                                text = it,
                                fontFamily = FontFamily.Monospace,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            cName = if (context.isTouchWiz) {
                                LockscreenShortcuts.ShortcutInfo.ComponentValues.fromString(
                                    context.buildDefaultSamsungLockScreenShortcuts()
                                ).getForSide(item.side)
                            } else {
                                null
                            }
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_undo_black_24dp),
                            contentDescription = stringResource(id = R.string.reset),
                        )
                    }
                }
            }
        }
    }
}
