package com.rw.tweaks.activities.tutorial

import android.content.Context
import android.content.Intent
import com.heinrichreimersoftware.materialintro.app.IntroActivity
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide
import com.rw.tweaks.R

class TutorialActivity : IntroActivity() {
    companion object {
        const val EXTRA_PERMISSIONS = "permissions"

        fun start(context: Context, vararg permissions: String) {
            val intent = Intent(context, TutorialActivity::class.java)
            intent.putExtra(EXTRA_PERMISSIONS, permissions)

            context.startActivity(intent)
        }
    }

    private val permissions by lazy { intent.getStringArrayExtra(EXTRA_PERMISSIONS) }

    private val introSlide by lazy {
        SimpleSlide.Builder()
            .title(R.string.adb_intro_title)
            .description(R.string.adb_intro_desc)
            .background(R.color.slide_1)
            .build()
    }

    private val osSlide by lazy {

    }
}