package com.zacharee1.systemuituner.compose

import android.text.Spanned
import androidx.appcompat.widget.AppCompatTextView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.activities.Intro
import io.noties.markwon.Markwon

@Composable
fun rememberIntroSlides(startReason: Intro.Companion.StartReason): List<IntroPage> {
    val context = LocalContext.current
    val slides = remember(startReason) {
        mutableStateListOf<IntroPage>()
    }

    if (slides.isEmpty()) {
        if (startReason == Intro.Companion.StartReason.INTRO) {
            slides.add(SimpleIntroPage(
                title = stringResource(R.string.intro_welcome),
                description = stringResource(R.string.intro_welcome_desc),
                icon = painterResource(id = R.drawable.ic_baseline_emoji_people_24),
                slideColor = { colorResource(id = R.color.slide_1) },
            ))

            val termsScrollState = rememberScrollState()

            slides.add(SimpleIntroPage(
                title = stringResource(id = R.string.intro_terms),
                description = stringResource(id = R.string.intro_terms_desc),
                icon = painterResource(id = R.drawable.ic_baseline_format_list_numbered_24),
                slideColor = { colorResource(id = R.color.slide_2) },
                scrollable = false,
                canMoveForward = {
                    !termsScrollState.canScrollForward
                },
                extraContent = {
                    OutlinedButton(onClick = { /* launch url */ }) {
                        Text(text = stringResource(id = R.string.view_online))
                    }

                    var termsText by remember {
                        mutableStateOf<Spanned?>(null)
                    }

                    LaunchedEffect(key1 = null) {
                        if (termsText == null) {
                            termsText = Markwon.create(context).toMarkdown(
                                context.resources.assets.open("terms.md").bufferedReader()
                                    .useLines { it.joinToString("\n") }
                            )
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
                            it.text = termsText
                        }
                    }
                }
            ))

            slides.add(SimpleIntroPage(
                title = stringResource(id = R.string.intro_disclaimer),
                description = stringResource(id = R.string.intro_disclaimer_desc),
                icon = painterResource(id = R.drawable.ic_baseline_priority_high_24),
                slideColor = { colorResource(id = R.color.slide_3) }
            ))


        }
    }

    return slides
}
