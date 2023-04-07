package com.zacharee1.systemuituner.compose.preferences

import android.os.Build
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.themeadapter.material3.Mdc3Theme
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.compose.rememberMonitorPreferenceState
import com.zacharee1.systemuituner.util.PrefManager
import com.zacharee1.systemuituner.util.SettingsInfo
import com.zacharee1.systemuituner.util.prefManager
import com.zacharee1.systemuituner.util.writeSettingsBulk
import kotlinx.coroutines.launch

@Composable
@Preview
fun TestPref() {
    Mdc3Theme {
        Surface {
            BasePreference(
                title = "Test Title",
                summary = "Let's make a very long summary to try to get it to truncate and see how the animation and such works hopefully it's good but we may have to do some work to make it look right in the end. Anyway, let's see how long we can make this. It needs to be longer to get a good idea of how it works so let's keep going shall we?",
                key = "test_pref",
                icon = painterResource(id = R.drawable.penguin),
                iconColor = colorResource(id = R.color.pref_color_1),
                dangerous = true,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseSettingsPreference(
    title: String,
    key: String,
    dialogContents: @Composable ColumnScope.(saveCallback: (Array<SettingsInfo>) -> Unit) -> Unit,
    modifier: Modifier = Modifier,
    minApi: Int = -1,
    maxApi: Int = -1,
    enabled: Boolean = true,
    iconColor: Color? = null,
    dangerous: Boolean = false,
    summary: String? = null,
    icon: Painter? = null,
    saveOption: Boolean = true,
    revertable: Boolean = false,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var showingDialog by remember(key) {
        mutableStateOf(false)
    }
    
    BasePreference(
        title = title,
        key = key,
        modifier = modifier,
        minApi = minApi,
        maxApi = maxApi,
        enabled = enabled,
        iconColor = iconColor,
        dangerous = dangerous,
        summary = summary,
        icon = icon,
        onClick = { showingDialog = true }
    )

    if (showingDialog) {
        ModalBottomSheet(onDismissRequest = { showingDialog = false }) {
            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                icon?.let {
                    Icon(painter = icon, contentDescription = title)
                }

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.size(8.dp))

            dialogContents(remember(key) {
                {
                    scope.launch {
                        context.writeSettingsBulk(
                            *it,
                            revertable = revertable,
                            saveOption = saveOption
                        )
                    }
                }
            })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasePreference(
    title: String,
    key: String,
    modifier: Modifier = Modifier,
    minApi: Int = -1,
    maxApi: Int = Int.MAX_VALUE,
    enabled: Boolean = true,
    iconColor: Color? = null,
    dangerous: Boolean = false,
    summary: String? = null,
    icon: Painter? = null,
    onClick: (() -> Unit)? = null
) {
    var summaryExpanded by remember(key) {
        mutableStateOf(false)
    }
    val summaryIconRotation by animateFloatAsState(
        targetValue = if (summaryExpanded) 0f else 180f,
        label = "SummaryExpanded $key"
    )
    val context = LocalContext.current
    val forceEnableAll by context.rememberMonitorPreferenceState(
        key = PrefManager.FORCE_ENABLE_ALL,
        value = { context.prefManager.forceEnableAll }
    )

    // TODO: Add API range messaging.
    val withinApiRange = Build.VERSION.SDK_INT >= minApi && Build.VERSION.SDK_INT <= maxApi
    val actuallyEnabled = forceEnableAll || (withinApiRange && enabled)

    OutlinedCard(
        onClick = { onClick?.invoke() },
        modifier = modifier,
        enabled = actuallyEnabled,
    ) {
        Log.e("SystemUITUner", "${LocalContentColor.current}")

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
                    .background(
                        if (actuallyEnabled) iconColor ?: Color.Transparent else (
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                    colorResource(id = R.color.icon_color)
                                } else {
                                    LocalContentColor.current
                                }
                                ).copy(alpha = 0.25f)
                    ),
                contentAlignment = Alignment.Center,
            ) {
                icon?.let {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                Brush.radialGradient(
                                    0f to Color.Black.copy(alpha = 0.7f),
                                    1f to Color.Transparent
                                )
                            )
                    )

                    Icon(
                        painter = icon,
                        contentDescription = title,
                        modifier = Modifier
                            .size(24.dp),
                        tint = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            colorResource(id = R.color.icon_color)
                        } else {
                            LocalContentColor.current
                        }
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (dangerous && actuallyEnabled) MaterialTheme.colorScheme.error else Color.Unspecified
                )

                summary?.let {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize()
                    ) {
                        Text(
                            text = summary,
                            maxLines = 3,
                            overflow = if (summaryExpanded) TextOverflow.Clip else TextOverflow.Ellipsis,
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

        Card(
            onClick = { summaryExpanded = !summaryExpanded },
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.outlinedCardColors(),
            elevation = CardDefaults.outlinedCardElevation(),
            enabled = actuallyEnabled,
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
