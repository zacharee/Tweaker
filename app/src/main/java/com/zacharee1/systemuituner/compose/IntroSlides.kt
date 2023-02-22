package com.zacharee1.systemuituner.compose

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.text.Spanned
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatTextView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.activities.intro.ComposeIntroActivity
import com.zacharee1.systemuituner.compose.components.IntroSpecialPermissionGrantGroup
import com.zacharee1.systemuituner.util.launchUrl
import com.zacharee1.systemuituner.util.prefManager
import io.noties.markwon.Markwon

@Composable
fun rememberIntroSlides(startReason: ComposeIntroActivity.Companion.StartReason): List<IntroPage> {
    val context = LocalContext.current
    val slides = remember(startReason) {
        mutableStateListOf<IntroPage>()
    }
    val termsScrollState = rememberScrollState()
    var rawTermsText by rememberSaveable {
        mutableStateOf<String?>(null)
    }
    var hasHitBottomOfTerms by rememberSaveable {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = termsScrollState.canScrollForward) {
        if (!termsScrollState.canScrollForward) {
            hasHitBottomOfTerms = true
        }
    }

    if (slides.isEmpty()) {
        if (startReason == ComposeIntroActivity.Companion.StartReason.INTRO) {
            slides.add(
                SimpleIntroPage(
                    title = { stringResource(R.string.intro_welcome) },
                    description = stringResource(R.string.intro_welcome_desc),
                    icon = { painterResource(id = R.drawable.ic_baseline_emoji_people_24) },
                    slideColor = { colorResource(id = R.color.slide_1) },
                )
            )

            slides.add(SimpleIntroPage(
                title = { stringResource(id = R.string.intro_terms) },
                description = stringResource(id = R.string.intro_terms_desc),
                icon = { painterResource(id = R.drawable.ic_baseline_format_list_numbered_24) },
                slideColor = { colorResource(id = R.color.slide_2) },
                scrollable = false,
                canMoveForward = { hasHitBottomOfTerms },
                horizontalTitleRow = true,
                fullWeightDescription = false,
                extraContent = {
                    OutlinedButton(onClick = {
                        context.launchUrl("https://github.com/zacharee/Tweaker/blob/master/app/src/main/assets/terms.md")
                    }) {
                        Text(text = stringResource(id = R.string.view_online))
                    }

                    LaunchedEffect(key1 = null) {
                        if (rawTermsText == null) {
                            rawTermsText = context.resources.assets.open("terms.md").bufferedReader()
                                .useLines { it.joinToString("\n") }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(termsScrollState)
                            .weight(1f)
                    ) {
                        AndroidView(
                            factory = { AppCompatTextView(it) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            it.text = Markwon.create(context).toMarkdown(rawTermsText ?: "")
                        }
                    }
                }
            ))

            slides.add(SimpleIntroPage(
                title = { stringResource(id = R.string.intro_disclaimer) },
                description = stringResource(id = R.string.intro_disclaimer_desc),
                icon = { painterResource(id = R.drawable.ic_baseline_priority_high_24) },
                slideColor = { colorResource(id = R.color.slide_3) }
            ))
        }

        if (startReason == ComposeIntroActivity.Companion.StartReason.WRITE_SECURE_SETTINGS ||
            startReason == ComposeIntroActivity.Companion.StartReason.INTRO
        ) {
            slides.add(SimpleIntroPage(
                title = { stringResource(id = R.string.intro_grant_wss) },
                description = stringResource(id = R.string.intro_grant_wss_desc),
                icon = { painterResource(id = R.drawable.ic_baseline_adb_24) },
                slideColor = { colorResource(id = R.color.slide_4) },
                canMoveForward = {
                    context.checkCallingOrSelfPermission(
                        android.Manifest.permission.WRITE_SECURE_SETTINGS
                    ) == PackageManager.PERMISSION_GRANTED
                },
                extraContent = {
                    IntroSpecialPermissionGrantGroup(permissions = arrayOf(android.Manifest.permission.WRITE_SECURE_SETTINGS))
                }
            ))
        }

        if (startReason == ComposeIntroActivity.Companion.StartReason.INTRO ||
            startReason == ComposeIntroActivity.Companion.StartReason.EXTRA_PERMISSIONS
        ) {
            slides.add(SimpleIntroPage(
                title = { stringResource(id = R.string.intro_grant_extra) },
                description = stringResource(id = R.string.intro_grant_extra_desc),
                icon = { painterResource(id = R.drawable.ic_baseline_adb_24) },
                slideColor = { colorResource(id = R.color.slide_5) },
                extraContent = {
                    IntroSpecialPermissionGrantGroup(
                        permissions = arrayOf(
                            android.Manifest.permission.DUMP,
                            android.Manifest.permission.PACKAGE_USAGE_STATS,
                        )
                    )
                }
            ))
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && (
                    startReason == ComposeIntroActivity.Companion.StartReason.INTRO ||
                            startReason == ComposeIntroActivity.Companion.StartReason.SYSTEM_ALERT_WINDOW
                    )
        ) {
            slides.add(SimpleIntroPage(
                title = { stringResource(id = R.string.intro_system_alert_window) },
                description = stringResource(id = R.string.intro_system_alert_window_desc),
                icon = { painterResource(id = R.drawable.ic_baseline_save_24) },
                slideColor = { colorResource(id = R.color.slide_6) },
                extraContent = {
                    OutlinedButton(
                        onClick = {
                            try {
                                context.startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                })
                            } catch (_: Exception) {
                            }
                        }
                    ) {
                        Text(text = stringResource(id = R.string.grant))
                    }
                }
            ))
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && (
                    startReason == ComposeIntroActivity.Companion.StartReason.NOTIFICATIONS ||
                            startReason == ComposeIntroActivity.Companion.StartReason.INTRO
                    )
        ) {
            slides.add(SimpleIntroPage(
                title = { stringResource(id = R.string.intro_allow_notifications) },
                description = stringResource(id = R.string.intro_allow_notifications_desc),
                icon = { painterResource(id = R.drawable.ic_baseline_notifications_24) },
                slideColor = { colorResource(id = R.color.slide_7) },
                extraContent = {
                    var isGranted by remember {
                        mutableStateOf(
                            context.checkCallingOrSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                                    == PackageManager.PERMISSION_GRANTED
                        )
                    }
                    val permissionRequester = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestPermission(),
                    ) { isGranted = it }

                    OutlinedButton(
                        onClick = {
                            permissionRequester.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                        },
                        enabled = !isGranted
                    ) {
                        Text(text = stringResource(id = if (isGranted) R.string.permission_grant_success else R.string.grant))
                    }
                }
            ))
        }

        if (startReason == ComposeIntroActivity.Companion.StartReason.INTRO ||
            startReason == ComposeIntroActivity.Companion.StartReason.CRASH_REPORTS
        ) {
            slides.add(SimpleIntroPage(
                title = { stringResource(id = R.string.intro_crash_reports) },
                description = stringResource(id = R.string.intro_crash_reports_desc),
                icon = { painterResource(id = R.drawable.baseline_bug_report_24) },
                slideColor = { colorResource(id = R.color.slide_4) },
                extraContent = {
                    var enabled by remember {
                        if (context.prefManager.enableCrashReports == null) {
                            context.prefManager.enableCrashReports = false
                        }
                        mutableStateOf(context.prefManager.enableCrashReports == true)
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
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
                                context.prefManager.enableCrashReports = it
                            }
                        )
                    }
                }
            ))
        }
        
        if (startReason == ComposeIntroActivity.Companion.StartReason.INTRO) {
            slides.add(SimpleIntroPage(
                title = { stringResource(id = R.string.intro_last) },
                description = stringResource(id = R.string.intro_last_desc),
                icon = { painterResource(id = R.drawable.foreground_unscaled) },
                slideColor = { colorResource(id = R.color.slide_8) }
            ))
        }
    }

    return slides
}
