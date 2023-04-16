package com.zacharee1.systemuituner.compose.preferences.layouts

import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.compose.components.SeekBar
import com.zacharee1.systemuituner.compose.rememberIntSettingsState
import com.zacharee1.systemuituner.data.SettingsType

private const val COUNT_DEF = 30
private const val INTERVAL_DEF = 1800000

@Composable
fun SMSLimitsLayout() {
    val context = LocalContext.current

    var smsLimitCount by context.rememberIntSettingsState(
        key = SettingsType.GLOBAL to Settings.Global.SMS_OUTGOING_CHECK_MAX_COUNT,
        def = COUNT_DEF,
        saveOption = true,
    )
    var smsCheckInterval by context.rememberIntSettingsState(
        key = SettingsType.GLOBAL to Settings.Global.SMS_OUTGOING_CHECK_INTERVAL_MS,
        def = INTERVAL_DEF,
        saveOption = true,
    )

    SeekBar(
        minValue = 0,
        maxValue = 1000,
        defaultValue = COUNT_DEF,
        scale = 1.0,
        value = smsLimitCount,
        onValueChanged = { smsLimitCount = it },
        title = stringResource(id = R.string.option_sms_limit_count),
    )

    SeekBar(
        minValue = 0,
        maxValue = Int.MAX_VALUE,
        defaultValue = INTERVAL_DEF,
        scale = 1.0,
        value = smsCheckInterval,
        onValueChanged = { smsCheckInterval = it },
        title = stringResource(id = R.string.option_sms_limit_interval),
        unit = "ms",
    )
}
