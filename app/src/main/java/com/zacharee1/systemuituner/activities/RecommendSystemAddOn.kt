package com.zacharee1.systemuituner.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.util.launchUrl
import dev.zwander.composeintroslider.IntroSlider
import dev.zwander.composeintroslider.SimpleIntroPage

class RecommendSystemAddOn : ComponentActivity() {
    companion object {
        fun start(context: Context) {
            val intent = Intent(context, RecommendSystemAddOn::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                val slides = listOf(SimpleIntroPage(
                    title = { stringResource(id = R.string.write_system_fail) },
                    description = { stringResource(id = R.string.write_system_fail_desc) },
                    icon = { painterResource(id = R.drawable.sad_face) },
                    slideColor = { colorResource(id = R.color.slide_1) },
                    contentColor = { colorResource(id = R.color.slide_1_content )},
                    extraContent = {
                        OutlinedButton(onClick = {
                            launchUrl("https://zwander.dev/dialog-systemuitunersystemsettingsadd-on")
                        }) {
                            Text(text = stringResource(id = R.string.write_system_fail_get_add_on))
                        }
                    }
                ))

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