package com.rw.tweaks.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import com.heinrichreimersoftware.materialintro.app.IntroActivity
import com.heinrichreimersoftware.materialintro.app.SlideFragment
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide.SimpleSlideFragment
import com.rw.tweaks.R
import com.rw.tweaks.util.dpAsPx
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
            FragmentSlide.Builder()
                .background(R.color.slide_1)
                .fragment(SimpleSlideFragmentOverride.newInstance(
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

        addSlide(
            FragmentSlide.Builder()
                .background(R.color.slide_3)
                .fragment(SimpleSlideFragmentOverride.newInstance(
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
            return context?.hasWss == true
        }
    }

    class SimpleSlideFragmentOverride private constructor() : SimpleSlideFragment() {
        companion object {
            private const val ARGUMENT_ID =
                "com.heinrichreimersoftware.materialintro.SimpleFragment.ARGUMENT_ID"
            private const val ARGUMENT_TITLE =
                "com.heinrichreimersoftware.materialintro.SimpleFragment.ARGUMENT_TITLE"
            private const val ARGUMENT_TITLE_RES =
                "com.heinrichreimersoftware.materialintro.SimpleFragment.ARGUMENT_TITLE_RES"
            private const val ARGUMENT_DESCRIPTION =
                "com.heinrichreimersoftware.materialintro.SimpleFragment.ARGUMENT_DESCRIPTION"
            private const val ARGUMENT_DESCRIPTION_RES =
                "com.heinrichreimersoftware.materialintro.SimpleFragment.ARGUMENT_DESCRIPTION_RES"
            private const val ARGUMENT_IMAGE_RES =
                "com.heinrichreimersoftware.materialintro.SimpleFragment.ARGUMENT_IMAGE_RES"
            private const val ARGUMENT_BACKGROUND_RES =
                "com.heinrichreimersoftware.materialintro.SimpleFragment.ARGUMENT_BACKGROUND_RES"
            private const val ARGUMENT_LAYOUT_RES =
                "com.heinrichreimersoftware.materialintro.SimpleFragment.ARGUMENT_LAYOUT_RES"

            private const val ARGUMENT_PERMISSIONS_REQUEST_CODE =
                "com.heinrichreimersoftware.materialintro.SimpleFragment.ARGUMENT_PERMISSIONS_REQUEST_CODE"

            fun newInstance(
                id: Long,
                title: CharSequence?, @StringRes titleRes: Int,
                description: CharSequence?, @StringRes descriptionRes: Int,
                @DrawableRes imageRes: Int, @ColorRes backgroundRes: Int,
                @LayoutRes layout: Int,
                permissionsRequestCode: Int
            ): SimpleSlideFragmentOverride? {
                val arguments = Bundle()
                arguments.putLong(ARGUMENT_ID, id)
                arguments.putCharSequence(ARGUMENT_TITLE, title)
                arguments.putInt(ARGUMENT_TITLE_RES, titleRes)
                arguments.putCharSequence(ARGUMENT_DESCRIPTION, description)
                arguments.putInt(ARGUMENT_DESCRIPTION_RES, descriptionRes)
                arguments.putInt(ARGUMENT_IMAGE_RES, imageRes)
                arguments.putInt(ARGUMENT_BACKGROUND_RES, backgroundRes)
                arguments.putInt(ARGUMENT_LAYOUT_RES, layout)
                arguments.putInt(
                    ARGUMENT_PERMISSIONS_REQUEST_CODE,
                    permissionsRequestCode
                )
                val fragment = SimpleSlideFragmentOverride()
                fragment.arguments = arguments
                return fragment
            }
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            imageView.layoutParams?.apply {
                width = requireContext().dpAsPx(128)

                imageView.layoutParams = this
            }
            imageView.scaleType = ImageView.ScaleType.FIT_CENTER
        }
    }
}