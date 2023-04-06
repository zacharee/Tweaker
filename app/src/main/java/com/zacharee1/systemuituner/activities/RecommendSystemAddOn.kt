package com.zacharee1.systemuituner.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.data.SettingsType
import com.zacharee1.systemuituner.util.launchUrl
import dev.zwander.composeintroslider.IntroSlider
import dev.zwander.composeintroslider.SimpleIntroPage

class RecommendSystemAddOn : ComponentActivity() {
    companion object {
        private const val EXTRA_KEY = "settings_key"
        private const val EXTRA_VALUE = "settings_value"

        fun start(context: Context, key: String, value: Any?) {
            val intent = Intent(context, RecommendSystemAddOn::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra(EXTRA_KEY, key)
            intent.putExtra(EXTRA_VALUE, value?.toString())
            context.startActivity(intent)
        }
    }

    private val settingsKey: String? by lazy { intent.getStringExtra(EXTRA_KEY) }
    private val settingsValue: String? by lazy { intent.getStringExtra(EXTRA_VALUE) }

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
                        Text(
                            text = stringResource(
                                id = R.string.custom_persistent_option_summary_template,
                                SettingsType.SYSTEM.name, settingsKey.toString(), settingsValue.toString()
                            )
                        )

                        Spacer(modifier = Modifier.size(8.dp))

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