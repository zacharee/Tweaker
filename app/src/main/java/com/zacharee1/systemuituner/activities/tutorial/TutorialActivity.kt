package com.zacharee1.systemuituner.activities.tutorial

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.zacharee1.systemuituner.compose.rememberTutorialSlides
import dev.zwander.composeintroslider.IntroSlider

class TutorialActivity : ComponentActivity() {
    companion object {
        const val EXTRA_PERMISSIONS = "permissions"

        fun start(context: Context, vararg permissions: String) {
            val intent = Intent(context, TutorialActivity::class.java)
            intent.putExtra(EXTRA_PERMISSIONS, permissions)

            context.startActivity(intent)
        }
    }

    private val permissions by lazy { intent.getStringArrayExtra(EXTRA_PERMISSIONS) ?: arrayOf() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                val slides = rememberTutorialSlides(permissions = permissions)

                IntroSlider(
                    pages = slides,
                    onExit = ::finish,
                    onDone = ::finish,
                    modifier = Modifier.fillMaxSize(),
                    backPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher,
                    normalizeElements = false,
                )
            }
        }
    }
}