package com.zacharee1.systemuituner.activities.intro

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import com.zacharee1.systemuituner.compose.rememberIntroSlides
import com.zacharee1.systemuituner.util.prefManager
import dev.zwander.composeintroslider.IntroSlider

class ComposeIntroActivity : ComponentActivity() {
    companion object {
        private const val EXTRA_START_REASON = "start_reason"

        fun start(context: Context, startReasons: Array<StartReason> = arrayOf(StartReason.INTRO)) {
            context.startActivity(Intent(context, ComposeIntroActivity::class.java).apply {
                putExtra(EXTRA_START_REASON, startReasons)
            })
        }

        fun startForResult(context: Context, launcher: ActivityResultLauncher<Intent>, startReasons: Array<StartReason> = arrayOf(StartReason.INTRO)) {
            launcher.launch(
                Intent(context, ComposeIntroActivity::class.java).apply {
                    putExtra(EXTRA_START_REASON, startReasons)
                }
            )
        }

        enum class StartReason {
            INTRO,
            SYSTEM_ALERT_WINDOW,
            NOTIFICATIONS,
            CRASH_REPORTS,
            WRITE_SECURE_SETTINGS,
            EXTRA_PERMISSIONS,
        }
    }

    private val startReasons by lazy { intent?.getSerializableExtra(EXTRA_START_REASON) as? Array<StartReason> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        setContent {
            MainContent(
                startReasons = startReasons ?: arrayOf(StartReason.INTRO),
                onFinish = { success ->
                    setResult(if (success) Activity.RESULT_OK else Activity.RESULT_CANCELED)
                    finish()
                }
            )
        }
    }
}

@Composable
@Preview
fun MainContent(
    startReasons: Array<ComposeIntroActivity.Companion.StartReason> =
        arrayOf(ComposeIntroActivity.Companion.StartReason.INTRO),
    onFinish: (success: Boolean) -> Unit = {},
) {
    MaterialTheme(
        colorScheme = darkColorScheme()
    ) {
        val slides = rememberIntroSlides(startReasons = startReasons)
        val context = LocalContext.current

        IntroSlider(
            pages = slides,
            onExit = { onFinish(false) },
            onDone = {
                context.prefManager.didIntro = true
                onFinish(true)
            },
            modifier = Modifier.fillMaxSize(),
            backPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher,
            normalizeElements = false,
        )
    }
}
