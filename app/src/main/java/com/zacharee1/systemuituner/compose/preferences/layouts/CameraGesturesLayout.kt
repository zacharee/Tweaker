package com.zacharee1.systemuituner.compose.preferences.layouts

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
import com.zacharee1.systemuituner.compose.components.CardSwitch
import com.zacharee1.systemuituner.compose.rememberBooleanSettingsState
import com.zacharee1.systemuituner.compose.rememberIntSettingsState
import com.zacharee1.systemuituner.data.SettingsType

private data class Gesture(
    @StringRes val label: Int,
    val key: String,
    val defaultValue: Int,
    val enabledValue: Int,
    val disabledValue: Int,
)

@Composable
fun CameraGesturesLayout() {
    val context = LocalContext.current

    val gestures = remember {
        listOf(
            Gesture(
                label = R.string.option_camera_gesture,
                key = Settings.Secure.CAMERA_GESTURE_DISABLED,
                defaultValue = 1,
                enabledValue = 0,
                disabledValue = 1,
            ),
            Gesture(
                label = R.string.option_camera_gesture_double_power,
                key = Settings.Secure.CAMERA_DOUBLE_TAP_POWER_GESTURE_DISABLED,
                defaultValue = 1,
                enabledValue = 0,
                disabledValue = 1,
            ),
            Gesture(
                label = R.string.option_camera_gesture_twist,
                key = Settings.Secure.CAMERA_DOUBLE_TWIST_TO_FLIP_ENABLED,
                defaultValue = 0,
                enabledValue = 1,
                disabledValue = 0,
            ),
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(gestures, { it.label }) { gesture ->
            var state by context.rememberBooleanSettingsState(
                keys = arrayOf(SettingsType.SECURE to gesture.key),
                enabledValue = gesture.enabledValue,
                disabledValue = gesture.disabledValue,
                defaultValue = gesture.defaultValue,
                saveOption = true,
            )

            CardSwitch(
                title = stringResource(id = gesture.label),
                checked = state,
                onCheckedChange = { state = it },
            )
        }
    }
}
