package com.zacharee1.systemuituner.activities.intro

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import com.google.accompanist.themeadapter.material3.Mdc3Theme
import com.zacharee1.systemuituner.activities.Intro
import com.zacharee1.systemuituner.compose.IntroSlider
import com.zacharee1.systemuituner.compose.rememberIntroSlides

class ComposeIntroActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MainContent()
        }
    }
}

@Composable
@Preview
fun MainContent() {
    (LocalLifecycleOwner.current as? Activity)?.window?.let {
        WindowCompat.setDecorFitsSystemWindows(it, false)
    }

    Mdc3Theme {
        val slides = rememberIntroSlides(startReason = Intro.Companion.StartReason.INTRO)
        
        IntroSlider(
            pages = slides,
            onExit = { /*TODO*/ },
            onDone = { /*TODO*/ },
            modifier = Modifier
        )
    }
}
