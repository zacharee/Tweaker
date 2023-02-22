package com.zacharee1.systemuituner.compose.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.util.prefManager

@Composable
fun CrashReportsToggle(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var enabled by remember {
        if (context.prefManager.enableCrashReports == null) {
            context.prefManager.enableCrashReports = false
        }
        mutableStateOf(context.prefManager.enableCrashReports == true)
    }

    LaunchedEffect(key1 = enabled) {
        context.prefManager.enableCrashReports = enabled
    }

    val interactionSource = remember {
        MutableInteractionSource()
    }

    Row(
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    enabled = !enabled
                }
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = stringResource(id = R.string.intro_crash_reports))

        Spacer(modifier = Modifier
            .weight(1f)
            .widthIn(min = 8.dp))

        Switch(
            checked = enabled,
            onCheckedChange = {
                enabled = it
            },
            interactionSource = interactionSource
        )
    }
}