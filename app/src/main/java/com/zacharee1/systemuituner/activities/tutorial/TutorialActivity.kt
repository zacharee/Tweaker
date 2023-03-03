package com.zacharee1.systemuituner.activities.tutorial

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.google.accompanist.themeadapter.material3.Mdc3Theme
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
            Mdc3Theme {
                val slides = rememberTutorialSlides(permissions = permissions)

                IntroSlider(
                    pages = slides,
                    onExit = ::finish,
                    onDone = ::finish,
                    modifier = Modifier.fillMaxSize(),
                    backPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher,
                    normalizeElements = Build.VERSION.SDK_INT < Build.VERSION_CODES.S,
                )
            }
        }
    }
}