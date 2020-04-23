package com.zacharee1.systemuituner.fragments.intro

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide
import com.zacharee1.systemuituner.util.dpAsPx

class SimpleSlideFragmentOverride() : SimpleSlide.SimpleSlideFragment() {
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
            val fragment =
                SimpleSlideFragmentOverride()
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