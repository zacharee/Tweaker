package com.rw.tweaks.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.heinrichreimersoftware.materialintro.app.IntroActivity
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide
import com.rw.tweaks.R
import com.rw.tweaks.fragments.intro.ExtraPermsSlide
import com.rw.tweaks.fragments.intro.SimpleSlideFragmentOverride
import com.rw.tweaks.fragments.intro.WSSSlide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

class Intro : IntroActivity(), CoroutineScope by MainScope() {
    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, Intro::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isButtonBackVisible = true
        buttonBackFunction = BUTTON_BACK_FUNCTION_BACK

        addSlide(
            FragmentSlide.Builder()
                .background(R.color.slide_1)
                .fragment(
                    SimpleSlideFragmentOverride.newInstance(
                    0, null, R.string.intro_welcome,
                    null, R.string.intro_welcome_desc, R.drawable.ic_baseline_emoji_people_24,
                    R.color.slide_1, R.layout.mi_fragment_simple_slide, 0
                ))
                .build()
        )

        val wss = FragmentSlide.Builder()
            .background(R.color.slide_2)
            .fragment(WSSSlide())
            .build()

        addSlide(wss)

        val extra = FragmentSlide.Builder()
            .background(R.color.slide_4)
            .fragment(ExtraPermsSlide())
            .build()

        addSlide(extra)

        addSlide(
            FragmentSlide.Builder()
                .background(R.color.slide_3)
                .fragment(
                    SimpleSlideFragmentOverride.newInstance(
                    0, null, R.string.intro_last,
                    null, R.string.intro_last_desc, R.drawable.foreground_unscaled,
                    R.color.slide_3, R.layout.mi_fragment_simple_slide, 0
                ))
                .build()
        )

        addOnNavigationBlockedListener { position, _ ->
            if (position == getSlidePosition(wss)) {
                Toast.makeText(this, R.string.wss_required_msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onBackPressed() {
        if (currentSlidePosition > 0) {
            previousSlide()
            return
        }
    }
}