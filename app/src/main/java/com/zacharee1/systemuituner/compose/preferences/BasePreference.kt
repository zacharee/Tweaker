package com.zacharee1.systemuituner.compose.preferences

import android.os.Build
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.themeadapter.material3.Mdc3Theme
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.compose.preferences.layouts.BottomSheetDialogLayout
import com.zacharee1.systemuituner.compose.rememberMonitorPreferenceState
import com.zacharee1.systemuituner.util.PrefManager
import com.zacharee1.systemuituner.util.prefManager

@Composable
@Preview
fun TestPref() {
    val context = LocalContext.current
    val forceEnableAll by context.rememberMonitorPreferenceState(
        key = PrefManager.FORCE_ENABLE_ALL,
        value = { context.prefManager.forceEnableAll }
    )
    val items = context.allScreens.filter {
        forceEnableAll || it.visible()
    }

    Mdc3Theme {
        Surface {
            LazyColumn(
                modifier = Modifier,
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(
                    items,
                    key = { it.key }
                ) { pref ->
                    when (pref) {
                        is SettingsPreferenceItem -> {
                            BaseSettingsPreference(info = pref)
                        }
                        is PreferenceItem -> {
                            BasePreference(info = pref)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BaseSettingsPreference(
    info: SettingsPreferenceItem,
    modifier: Modifier = Modifier
) {
    var showingDialog by remember(info.key) {
        mutableStateOf(false)
    }

    BasePreference(
        modifier = modifier,
        info = PreferenceItem(
            title = info.title,
            key = info.key,
            minApi = info.minApi,
            maxApi = info.maxApi,
            enabled = info.enabled,
            iconColor = info.iconColor,
            dangerous = info.dangerous,
            summary = info.summary,
            icon = info.icon,
            onClick = { showingDialog = true },
            visible = info.visible,
            writeKeys = info.writeKeys,
            persistable = info.persistable,
        )
    )

    if (showingDialog) {
        BottomSheetDialogLayout(
            title = info.title,
            onDismissRequest = { showingDialog = false },
            icon = info.icon,
            contents = info.dialogContents
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasePreference(
    info: PreferenceItem,
    modifier: Modifier = Modifier,
) {
    var summaryExpanded by remember(info.key) {
        mutableStateOf(false)
    }
    var showSummaryExpander by remember(info.key) {
        mutableStateOf(false)
    }
    val summaryIconRotation by animateFloatAsState(
        targetValue = if (summaryExpanded) 0f else 180f,
        label = "SummaryExpanded ${info.key}"
    )
    val context = LocalContext.current
    val forceEnableAll by context.rememberMonitorPreferenceState(
        key = PrefManager.FORCE_ENABLE_ALL,
        value = { context.prefManager.forceEnableAll }
    )

    // TODO: Add API range messaging.
    val withinApiRange =
        Build.VERSION.SDK_INT >= info.minApi && Build.VERSION.SDK_INT <= info.maxApi
    val actuallyEnabled = forceEnableAll || (withinApiRange && info.enabled())

    val iconBackgroundColor = if (actuallyEnabled) {
        info.iconColor?.let { colorResource(id = it) } ?: Color.Transparent
    } else {
        (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            colorResource(id = R.color.icon_color)
        } else {
            LocalContentColor.current
        }).copy(alpha = 0.25f)
    }
    val iconForegroundColor = contentColorFor(backgroundColor = iconBackgroundColor)

    OutlinedCard(
        onClick = { info.onClick?.invoke() },
        modifier = modifier,
        enabled = actuallyEnabled,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, start = 8.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .size(48.dp)
                    .background(iconBackgroundColor),
                contentAlignment = Alignment.Center,
            ) {
                info.icon?.let { icon ->
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                Brush.radialGradient(
                                    0f to Color.Black.copy(alpha = 0.25f),
                                    1f to Color.Transparent
                                )
                            )
                    )

                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = info.title,
                        modifier = Modifier
                            .size(24.dp),
                        tint = iconForegroundColor
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = info.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (info.dangerous && actuallyEnabled) MaterialTheme.colorScheme.error else Color.Unspecified
                )

                info.summary?.let { summary ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize()
                    ) {
                        Text(
                            text = summary,
                            maxLines = 3,
                            overflow = if (summaryExpanded) TextOverflow.Clip else TextOverflow.Ellipsis,
                            onTextLayout = { result ->
                                showSummaryExpander = result.hasVisualOverflow
                            }
                        )

                        androidx.compose.animation.AnimatedVisibility(
                            visible = summaryExpanded,
                            enter = expandVertically(expandFrom = Alignment.Top),
                            exit = shrinkVertically(shrinkTowards = Alignment.Top),
                        ) {
                            Text(
                                text = summary,
                                maxLines = Int.MAX_VALUE,
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.size(8.dp))

        if (showSummaryExpander) {
            CompositionLocalProvider(
                LocalMinimumInteractiveComponentEnforcement provides false
            ) {
                OutlinedCard(
                    onClick = { summaryExpanded = !summaryExpanded },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = actuallyEnabled,
                    border = BorderStroke(0.dp, Color.Transparent)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_up),
                        contentDescription = null,
                        modifier = Modifier
                            .rotate(summaryIconRotation)
                            .align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}
