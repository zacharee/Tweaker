package com.zacharee1.systemuituner.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.heinrichreimersoftware.materialintro.app.IntroActivity
import com.heinrichreimersoftware.materialintro.app.SlideFragment
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide
import com.zacharee1.systemuituner.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

class ExtraPermsRetroactive : IntroActivity(), CoroutineScope by MainScope() {
    companion object {
        const val EXTRA_FRAGMENT_CLASS = "FRAGMENT_CLASS"

        fun start(context: Context, fragmentClass: Class<out SlideFragment>) {
            val intent = Intent(context, ExtraPermsRetroactive::class.java)
            intent.putExtra(EXTRA_FRAGMENT_CLASS, fragmentClass)

            context.startActivity(intent)
        }
    }

    private val fragmentClass by lazy { intent.getSerializableExtra(EXTRA_FRAGMENT_CLASS) as Class<*>? }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (fragmentClass == null) {
            finish()
            return
        }

        fragmentClass?.let {
            addSlide(
                FragmentSlide.Builder()
                    .background(R.color.slide_5)
                    .fragment(it.newInstance() as Fragment)
                    .build()
            )
        }
    }
}