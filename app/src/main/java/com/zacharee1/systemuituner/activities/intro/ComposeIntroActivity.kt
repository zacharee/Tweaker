package com.zacharee1.systemuituner.activities.intro

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import com.google.accompanist.themeadapter.material3.Mdc3Theme
import com.zacharee1.systemuituner.compose.IntroSlider
import com.zacharee1.systemuituner.compose.rememberIntroSlides
import com.zacharee1.systemuituner.util.prefManager

class ComposeIntroActivity : ComponentActivity() {
    companion object {
        private const val EXTRA_START_REASON = "start_reason"

        fun start(context: Context, startReason: StartReason = StartReason.INTRO) {
            context.startActivity(Intent(context, ComposeIntroActivity::class.java).apply {
                putExtra(EXTRA_START_REASON, startReason)
            })
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

    private val startReason by lazy { intent?.getSerializableExtra(EXTRA_START_REASON) as? StartReason }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        setContent {
            MainContent(
                startReason = startReason ?: StartReason.INTRO,
                onFinish = ::finish
            )
        }
    }
}

@Composable
@Preview
fun MainContent(
    startReason: ComposeIntroActivity.Companion.StartReason = ComposeIntroActivity.Companion.StartReason.INTRO,
    onFinish: () -> Unit = {},
) {
    Mdc3Theme {
        val slides = rememberIntroSlides(startReason = startReason)
        val context = LocalContext.current

        IntroSlider(
            pages = slides,
            onExit = onFinish,
            onDone = {
                context.prefManager.didIntro = true
                onFinish()
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}
