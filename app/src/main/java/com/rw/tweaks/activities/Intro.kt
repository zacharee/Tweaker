package com.rw.tweaks.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.heinrichreimersoftware.materialintro.app.IntroActivity
import com.heinrichreimersoftware.materialintro.app.OnNavigationBlockedListener
import com.heinrichreimersoftware.materialintro.app.SlideFragment
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide
import com.rw.tweaks.R
import com.rw.tweaks.util.hasWss
import eu.chainfire.libsuperuser.Shell
import kotlinx.android.synthetic.main.wss_slide.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

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
            SimpleSlide.Builder()
                .title(R.string.intro_welcome)
                .description(R.string.intro_welcome_desc)
                .image(R.drawable.header)
                .background(R.color.slide_1)
                .scrollable(true)
                .build()
        )

        val wss = FragmentSlide.Builder()
            .background(R.color.slide_2)
            .fragment(WSSSlide())
            .build()

        addSlide(wss)

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

    class WSSSlide : SlideFragment(), CoroutineScope by MainScope() {
        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            return inflater.inflate(R.layout.wss_slide, container, false)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            view.apply {
                grant.setOnClickListener {
                    launch {
                        val hasRoot = async { Shell.SU.available() }

                        if (hasRoot.await()) {
                            val result = async {
                                Shell.Pool.SU.run("pm grant ${requireContext().packageName} ${android.Manifest.permission.WRITE_SECURE_SETTINGS}")
                            }

                            result.await()
                        } else {
                            AlertDialog.Builder(requireActivity())
                                .setTitle(R.string.no_root_title)
                                .setMessage(R.string.no_root_msg)
                                .setPositiveButton(android.R.string.ok, null)
                                .show()
                        }
                    }
                }
            }
        }

        override fun canGoForward(): Boolean {
            return requireContext().hasWss
        }
    }
}