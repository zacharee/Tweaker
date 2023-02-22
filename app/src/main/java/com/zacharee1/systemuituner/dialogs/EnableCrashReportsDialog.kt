package com.zacharee1.systemuituner.dialogs

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.themeadapter.material3.Mdc3Theme
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.compose.components.CrashReportsToggle

class EnableCrashReportsDialog(context: Context) : RoundedBottomSheetDialog(context) {
    init {
        setTitle(R.string.intro_crash_reports)
        setLayout(ComposeView(context).apply {
            setContent {
                Mdc3Theme {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.crash_reports_desc)
                        )

                        CrashReportsToggle(
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        })
        setPositiveButton(android.R.string.ok, null)
    }
}