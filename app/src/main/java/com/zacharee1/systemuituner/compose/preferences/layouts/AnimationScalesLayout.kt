package com.zacharee1.systemuituner.compose.preferences.layouts

import android.provider.Settings
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
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
import com.zacharee1.systemuituner.compose.rememberFloatSettingsState
import com.zacharee1.systemuituner.data.SettingsType

private data class AnimationOption(
    @StringRes val label: Int,
    val key: String,
)

@Composable
fun AnimationScalesLayout() {
    val context = LocalContext.current

    val options = remember {
        listOf(
            AnimationOption(
                label = R.string.option_animator_duration_scale,
                key = Settings.Global.ANIMATOR_DURATION_SCALE,
            ),
            AnimationOption(
                label = R.string.option_window_animation_scale,
                key = Settings.Global.WINDOW_ANIMATION_SCALE,
            ),
            AnimationOption(
                label = R.string.option_transition_animation_scale,
                key = Settings.Global.TRANSITION_ANIMATION_SCALE,
            ),
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(options, { it.label }) { option ->
            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .padding(8.dp),
                ) {
                    var state by context.rememberFloatSettingsState(
                        key = SettingsType.GLOBAL to option.key,
                        saveOption = true,
                        def = 1f
                    )

                    Text(
                        text = stringResource(id = option.label),
                        style = MaterialTheme.typography.titleMedium,
                    )

                    SeekBar(
                        minValue = 0,
                        maxValue = 5,
                        defaultValue = 1,
                        scale = 0.01,
                        value = state,
                        onValueChanged = { state = it },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}
