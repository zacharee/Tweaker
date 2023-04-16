package com.zacharee1.systemuituner.compose.preferences.layouts

import android.provider.Settings
import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.compose.rememberListSettingsState
import com.zacharee1.systemuituner.data.SettingsType

private const val CELL = "cell"
private const val BT = "bluetooth"
private const val WIFI = "wifi"
private const val NFC = "nfc"
private const val WMX = "wimax"

private data class RadioInfo(
    @StringRes val name: Int,
    val id: String,
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AirplaneModeRadiosLayout() {
    val context = LocalContext.current
    
    var blocklist by context.rememberListSettingsState(
        key = SettingsType.GLOBAL to Settings.Global.AIRPLANE_MODE_RADIOS,
        saveOption = true,
    )
    var toggleable by context.rememberListSettingsState(
        key = SettingsType.GLOBAL to Settings.Global.AIRPLANE_MODE_TOGGLEABLE_RADIOS,
        saveOption = true,
    )
    
    val radios = remember {
        listOf(
            RadioInfo(
                R.string.option_airplane_mode_radio_cell,
                CELL,
            ),
            RadioInfo(
                R.string.option_airplane_mode_radio_bluetooth,
                BT,
            ),
            RadioInfo(
                R.string.option_airplane_mode_radio_wifi,
                WIFI,
            ),
            RadioInfo(
                R.string.option_airplane_mode_radio_nfc,
                NFC,
            ),
            RadioInfo(
                R.string.option_airplane_mode_radio_wimax,
                WMX,
            ),
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        stickyHeader {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
            ) {
                Text(
                    text = stringResource(id = R.string.radio),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Start,
                )

                Text(
                    text = stringResource(id = R.string.feature_header_airplane_mode_radios),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(0.5f),
                    textAlign = TextAlign.Center,
                )

                Text(
                    text = stringResource(id = R.string.feature_header_airplane_mode_toggleable_radios),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(0.5f),
                    textAlign = TextAlign.Center,
                )
            }
        }

        items(radios, { it.id }) { radio ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(id = radio.name),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Start,
                )

                Checkbox(
                    checked = !blocklist.contains(radio.id),
                    onCheckedChange = {
                        blocklist = if (it) {
                            blocklist - radio.id
                        } else {
                            blocklist + radio.id
                        }
                    },
                    modifier = Modifier.weight(0.5f),
                )

                Checkbox(
                    checked = toggleable.contains(radio.id),
                    onCheckedChange = {
                        toggleable = if (it) {
                            toggleable + radio.id
                        } else {
                            toggleable - radio.id
                        }
                    },
                    modifier = Modifier.weight(0.5f),
                )
            }
        }
    }
}
