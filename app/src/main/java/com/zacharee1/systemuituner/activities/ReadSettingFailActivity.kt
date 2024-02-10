package com.zacharee1.systemuituner.activities

import android.content.Context
import android.content.Intent
import android.graphics.Color
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
import androidx.core.view.WindowCompat
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.data.SettingsType
import com.zacharee1.systemuituner.util.launchUrl
import com.zacharee1.systemuituner.util.openShizuku
import com.zacharee1.systemuituner.util.openShizukuWebsite
import com.zacharee1.systemuituner.util.shizukuServiceManager
import dev.zwander.composeintroslider.IntroSlider
import dev.zwander.composeintroslider.SimpleIntroPage
import java.io.Serializable

class ReadSettingFailActivity : ComponentActivity() {
    companion object {
        private const val EXTRA_TYPE = "settings_type"
        private const val EXTRA_KEY = "settings_key"

        fun start(context: Context, type: SettingsType, key: String?) {
            val intent = Intent(context, ReadSettingFailActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra(EXTRA_TYPE, type as Serializable)
            intent.putExtra(EXTRA_KEY, key)
            context.startActivity(intent)
        }
    }

    private val settingsType: SettingsType? by lazy { intent.getSerializableExtra(EXTRA_TYPE) as? SettingsType? }
    private val settingsKey: String? by lazy { intent.getStringExtra(EXTRA_KEY) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                val slides = listOf(SimpleIntroPage(
                    title = { stringResource(id = R.string.read_setting_fail) },
                    description = { stringResource(id = R.string.read_setting_fail_desc) },
                    icon = { painterResource(id = R.drawable.sad_face) },
                    slideColor = { colorResource(id = R.color.slide_1) },
//                    contentColor = { colorResource(id = R.color.slide_1_content )},
                    extraContent = {
                        Text(
                            text = stringResource(
                                id = R.string.settings_read_template,
                                settingsType?.name.toString(), settingsKey.toString()
                            )
                        )

                        Spacer(modifier = Modifier.size(8.dp))

                        OutlinedButton(onClick = {
                            launchUrl("https://github.com/zacharee/SystemUITunerSystemSettings")
                        }) {
                            Text(text = stringResource(id = R.string.write_system_fail_get_add_on))
                        }

                        if (shizukuServiceManager.isShizukuInstalled) {
                            OutlinedButton(onClick = {
                                openShizuku()
                            }) {
                                Text(text = stringResource(id = R.string.open_shizuku))
                            }
                        } else {
                            OutlinedButton(onClick = {
                                openShizukuWebsite()
                            }) {
                                Text(text = stringResource(id = R.string.download_shizuku))
                            }
                        }
                    }
                ))

                IntroSlider(
                    pages = slides,
                    onExit = ::finish,
                    onDone = ::finish,
                    modifier = Modifier.fillMaxSize(),
                    backPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher,
                    normalizeElements = true,
                )
            }
        }
    }
}