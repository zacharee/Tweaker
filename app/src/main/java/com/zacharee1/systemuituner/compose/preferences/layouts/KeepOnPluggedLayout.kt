package com.zacharee1.systemuituner.compose.preferences.layouts

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
import com.zacharee1.systemuituner.compose.rememberSettingsState
import com.zacharee1.systemuituner.data.SettingsType
import com.zacharee1.systemuituner.util.getSetting

private data class Option(
    @StringRes val label: Int,
    val checkedFlag: Int,
)

@Composable
fun KeepOnPluggedLayout() {
    val context = LocalContext.current
    var state by context.rememberSettingsState(
        key = SettingsType.GLOBAL to Settings.Global.STAY_ON_WHILE_PLUGGED_IN,
        value = { context.getSetting(SettingsType.GLOBAL, Settings.Global.STAY_ON_WHILE_PLUGGED_IN, 0)?.toIntOrNull() ?: 0 },
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
            )
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(options, { it.label }) { option ->
            val selected = state and option.checkedFlag != 0

            SelectableCard(
                label = stringResource(id = option.label),
                selected = selected,
                onClick = {
                    state = if (selected) {
                        state and option.checkedFlag.inv()
                    } else {
                        state or option.checkedFlag
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
