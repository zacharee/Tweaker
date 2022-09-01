package com.zacharee1.systemuituner.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import com.heinrichreimersoftware.materialintro.app.IntroActivity
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.fragments.intro.ExtraPermsSlide
import com.zacharee1.systemuituner.fragments.intro.SimpleSlideFragmentOverride
import com.zacharee1.systemuituner.fragments.intro.TermsSlide
import com.zacharee1.systemuituner.fragments.intro.WSSSlide
import com.zacharee1.systemuituner.util.prefManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

class Intro : IntroActivity(), CoroutineScope by MainScope() {
    companion object {
        private const val EXTRA_START_REASON = "start_reason"

        fun start(context: Context, startReason: StartReason = StartReason.INTRO) {
            context.startActivity(Intent(context, Intro::class.java).apply {
                putExtra(EXTRA_START_REASON, startReason)
            })
        }

        enum class StartReason {
            INTRO,
            SYSTEM_ALERT_WINDOW,
            NOTIFICATIONS
        }
    }

    private val startReason by lazy { intent?.getSerializableExtra(EXTRA_START_REASON) as? StartReason }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (startReason == null) {
            finish()
            return
        }

        isButtonBackVisible = true
        buttonBackFunction = BUTTON_BACK_FUNCTION_BACK

        if (startReason == StartReason.INTRO) {
            addSlide(
                FragmentSlide.Builder()
                    .background(R.color.slide_1)
                    .fragment(
                        SimpleSlideFragmentOverride.newInstance(
                            0, null, R.string.intro_welcome,
                            null, R.string.intro_welcome_desc, R.drawable.ic_baseline_emoji_people_24,
                            R.color.slide_1, R.layout.mi_fragment_simple_slide_scrollable, 0
                        )
                    )
                    .build()
            )

            addSlide(
                FragmentSlide.Builder()
                    .background(R.color.slide_2)
                    .fragment(TermsSlide())
                    .build()
            )

            addSlide(
                FragmentSlide.Builder()
                    .background(R.color.slide_3)
                    .fragment(
                        SimpleSlideFragmentOverride.newInstance(
                            0,
                            null,
                            R.string.intro_disclaimer,
                            null,
                            R.string.intro_disclaimer_desc,
                            R.drawable.ic_baseline_priority_high_24,
                            R.color.slide_3,
                            R.layout.mi_fragment_simple_slide_scrollable,
                            0
                        )
                    )
                    .build()
            )

            val wss = FragmentSlide.Builder()
                .background(R.color.slide_4)
                .fragment(WSSSlide())
                .build()

            addSlide(wss)

            val extra = FragmentSlide.Builder()
                .background(R.color.slide_5)
                .fragment(ExtraPermsSlide())
                .build()

            addSlide(extra)

            addOnNavigationBlockedListener { position, _ ->
                if (position == getSlidePosition(wss)) {
                    Toast.makeText(this, R.string.wss_required_msg, Toast.LENGTH_SHORT).show()
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S || startReason == StartReason.SYSTEM_ALERT_WINDOW) {
            addSlide(
                FragmentSlide.Builder()
                    .background(R.color.slide_6)
                    .fragment(
                        SimpleSlideFragmentOverride.newInstance(
                            0, null, R.string.intro_system_alert_window,
                            null, R.string.intro_system_alert_window_desc, R.drawable.ic_baseline_save_24,
                            R.color.slide_6, R.layout.mi_fragment_simple_slide, 0
                        )
                    )
                    .buttonCtaLabel(R.string.grant)
                    .buttonCtaClickListener {
                        try {
                            startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            })
                        } catch (_: Exception) {}
                    }
                    .build()
            )
        }

        @SuppressLint("InlinedApi")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU || startReason == StartReason.NOTIFICATIONS) {
            addSlide(
                FragmentSlide.Builder()
                    .background(R.color.slide_7)
                    .fragment(
                        SimpleSlideFragmentOverride.newInstance(
                            0, null, R.string.intro_allow_notifications,
                            null, R.string.intro_allow_notifications_desc,
                            R.drawable.ic_baseline_notifications_24, R.color.slide_7,
                            R.layout.mi_fragment_simple_slide, 0
                        )
                    )
                    .buttonCtaLabel(R.string.grant)
                    .buttonCtaClickListener {
                        requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 100)
                    }
                    .build()
            )
        }

        addSlide(
            FragmentSlide.Builder()
                .background(R.color.slide_8)
                .fragment(
                    SimpleSlideFragmentOverride.newInstance(
                        0, null, R.string.intro_last,
                        null, R.string.intro_last_desc, R.drawable.foreground_unscaled,
                        R.color.slide_8, R.layout.mi_fragment_simple_slide, 0
                    )
                )
                .build()
        )
    }

    override fun onBackPressed() {
        if (currentSlidePosition > 0) {
            previousSlide()
            return
        }
    }

    @SuppressLint("DiscouragedPrivateApi")
    override fun onDestroy() {
        super.onDestroy()

        if (Activity::class.java.getDeclaredField("mResultCode")
                .apply { isAccessible = true }.get(this) == Activity.RESULT_OK) {

            when (startReason) {
                StartReason.SYSTEM_ALERT_WINDOW -> prefManager.sawSystemAlertWindow = true
                StartReason.NOTIFICATIONS -> prefManager.sawNotificationsAlert = true
                StartReason.INTRO -> {
                    prefManager.sawSystemAlertWindow = true
                    prefManager.sawNotificationsAlert = true
                }
                else -> {}
            }
        }

        cancel()
    }
}