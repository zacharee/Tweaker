package com.zacharee1.systemuituner.compose.preferences.layouts

import android.os.Build
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.compose.components.CardSwitch
import com.zacharee1.systemuituner.compose.components.SeekBar
import com.zacharee1.systemuituner.compose.components.SelectableCard
import com.zacharee1.systemuituner.compose.rememberBooleanSettingsState
import com.zacharee1.systemuituner.compose.rememberIntSettingsState
import com.zacharee1.systemuituner.data.SettingsType
import com.zacharee1.systemuituner.data.defaultNightTemp
import com.zacharee1.systemuituner.views.NightModeView

@Composable
fun NightModeLayout() {
    val isNougatMr1 = remember {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        if (isNougatMr1) {
            NightDisplayLayout(
                modifier = Modifier.fillMaxWidth(),
            )
        } else {
            TwilightModeLayout(
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun TwilightModeLayout(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var state by context.rememberIntSettingsState(
        key = SettingsType.SECURE to NightModeView.TWILIGHT_MODE
    )

    val items = remember {
        listOf(
            TwilightInfo(
                label = R.string.disabled,
                value = NightModeView.TWILIGHT_OFF,
            ),
            TwilightInfo(
                label = R.string.enable,
                value = NightModeView.TWILIGHT_ON,
            ),
            TwilightInfo(
                label = R.string.auto,
                value = NightModeView.TWILIGHT_AUTO,
            ),
        )
    }

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(items, { it.label }) { item ->
            SelectableCard(
                label = stringResource(id = item.label),
                selected = state == item.value,
                onClick = {
                    state = item.value
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun NightDisplayLayout(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val defaultTemp = remember {
        context.defaultNightTemp
    }

    var activatedState by context.rememberBooleanSettingsState(
        keys = arrayOf(SettingsType.SECURE to NightModeView.NIGHT_DISPLAY_ACTIVATED),
        revertable = true,
    )
    var autoState by context.rememberBooleanSettingsState(
        keys = arrayOf(SettingsType.SECURE to NightModeView.NIGHT_DISPLAY_AUTO_MODE),
        revertable = true,
    )
    var tempState by context.rememberIntSettingsState(
        key = SettingsType.SECURE to NightModeView.NIGHT_DISPLAY_COLOR_TEMPERATURE,
        def = defaultTemp,
    )

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        CardSwitch(
            title = stringResource(id = R.string.enabled),
            checked = activatedState,
            onCheckedChange = {
                activatedState = it
            },
            modifier = Modifier.fillMaxWidth(),
        )

        CardSwitch(
            title = stringResource(id = R.string.auto),
            checked = autoState,
            onCheckedChange = {
                autoState = it
            },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.size(0.dp))

        SeekBar(
            minValue = 0,
            maxValue = 10000,
            defaultValue = defaultTemp,
            scale = 1.0,
            value = tempState,
            onValueChanged = {
                tempState = it.toInt()
            },
            title = stringResource(id = R.string.night_display_temp),
        )
    }
}

private data class TwilightInfo(
    @StringRes val label: Int,
    val value: Int,
)
