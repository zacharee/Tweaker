package com.zacharee1.systemuituner.views

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.zacharee1.systemuituner.databinding.TermsSlideBinding
import com.zacharee1.systemuituner.util.launchUrl
import io.noties.markwon.Markwon

class TermsFragmentRoot(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {
    private var isAtBottom = false

    private val binding by lazy { TermsSlideBinding.bind(this) }

    override fun onFinishInflate() {
        super.onFinishInflate()

        val markwon = Markwon.create(context)
        val termsString = StringBuilder()

        context.resources.assets.open("terms.md").bufferedReader().useLines { lines ->
            lines.forEach { termsString.append("$it\n") }
        }

        markwon.setMarkdown(binding.termsView, termsString.toString())

        binding.viewOnline.setOnClickListener {
            isAtBottom = true
            context.launchUrl("https://github.com/zacharee/Tweaker/blob/master/app/src/main/assets/terms.md")
        }

        binding.termsHolder.setOnScrollChangeListener { _, _, _, _, _ ->
            if (!binding.termsHolder.canScrollVertically(1)) {
                isAtBottom = true
            }
        }
    }

    fun canGoForward(): Boolean {
        return isAtBottom
    }
}