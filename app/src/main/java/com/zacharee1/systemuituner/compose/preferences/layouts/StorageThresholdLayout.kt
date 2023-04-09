package com.zacharee1.systemuituner.compose.preferences.layouts

import android.provider.Settings
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.compose.components.SeekBar
import com.zacharee1.systemuituner.compose.rememberNumberSettingsState
import com.zacharee1.systemuituner.data.SettingsType

private data class ThresholdOption(
    @StringRes val label: Int,
    val key: String,
    val scale: Number,
    val defaultValue: Number,
    val minValue: Number,
    val maxValue: Number,
    val unit: String,
)

@Composable
fun StorageThresholdLayout() {
    val context = LocalContext.current

    val options = remember {
        listOf(
            ThresholdOption(
                label = R.string.option_storage_threshold_percentage,
                key = Settings.Global.SYS_STORAGE_THRESHOLD_PERCENTAGE,
                scale = 1,
                minValue = 0,
                maxValue = 100,
                defaultValue = 5,
                unit = "%",
            ),
            ThresholdOption(
                label = R.string.option_storage_threshold_max_bytes,
                key = Settings.Global.SYS_STORAGE_THRESHOLD_MAX_BYTES,
                scale = 0.000001,
                minValue = 0,
                maxValue = 2000000000,
                defaultValue = 500,
                unit = "MB",
            )
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(options, { it.label }) { option ->
            var state by context.rememberNumberSettingsState(
                key = SettingsType.GLOBAL to option.key,
                def = option.defaultValue,
                saveOption = true,
            )

            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                ) {
                    Text(
                        text = stringResource(id = option.label),
                        style = MaterialTheme.typography.titleMedium,
                    )

                    SeekBar(
                        minValue = option.minValue,
                        maxValue = option.maxValue,
                        defaultValue = option.defaultValue,
                        scale = option.scale.toDouble(),
                        value = state,
                        onValueChanged = { state = it },
                        unit = option.unit,
                    )
                }
            }
        }
    }
}
