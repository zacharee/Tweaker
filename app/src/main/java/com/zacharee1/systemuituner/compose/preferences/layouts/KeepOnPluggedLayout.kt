package com.zacharee1.systemuituner.compose.preferences.layouts

import android.annotation.SuppressLint
import android.os.BatteryManager
import android.provider.Settings
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.zacharee1.systemuituner.compose.components.SelectableCard
import com.zacharee1.systemuituner.compose.rememberIntSettingsState
import com.zacharee1.systemuituner.data.SettingsType

private data class Option(
    @StringRes val label: Int,
    val checkedFlag: Int,
)

@SuppressLint("InlinedApi")
@Composable
fun KeepOnPluggedLayout() {
    val context = LocalContext.current
    var state by context.rememberIntSettingsState(
        key = SettingsType.GLOBAL to Settings.Global.STAY_ON_WHILE_PLUGGED_IN,
        def = 0,
        saveOption = true,
    )

    val options = remember {
        listOf(
            Option(
                label = R.string.option_keep_awake_on_ac,
                checkedFlag = BatteryManager.BATTERY_PLUGGED_AC,
            ),
            Option(
                label = R.string.option_keep_awake_on_usb,
                checkedFlag = BatteryManager.BATTERY_PLUGGED_USB,
            ),
            Option(
                label = R.string.option_keep_awake_on_wireless,
                checkedFlag = BatteryManager.BATTERY_PLUGGED_WIRELESS,
            ),
            Option(
                label = R.string.option_keep_awake_on_dock,
                checkedFlag = BatteryManager.BATTERY_PLUGGED_DOCK,
            ),
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(options, { it.label }) { option ->
            val selected = state.toInt() and option.checkedFlag != 0

            SelectableCard(
                label = stringResource(id = option.label),
                selected = selected,
                onClick = {
                    val currentState = state
                    val newState = if (selected) {
                        currentState.toInt() and option.checkedFlag.inv()
                    } else {
                        currentState.toInt() or option.checkedFlag
                    }

                    state = newState
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
