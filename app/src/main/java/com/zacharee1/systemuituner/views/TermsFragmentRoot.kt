package com.zacharee1.systemuituner.views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.constraintlayout.widget.ConstraintLayout
import com.zacharee1.systemuituner.util.launchUrl
import io.noties.markwon.Markwon
import kotlinx.android.synthetic.main.terms_slide.view.*

class TermsFragmentRoot(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {
    private var isAtBottom = false

    override fun onFinishInflate() {
        super.onFinishInflate()

        val markwon = Markwon.create(context)
        val termsString = StringBuilder()

        context.resources.assets.open("terms.md").bufferedReader().useLines { lines ->
            lines.forEach { termsString.append("$it\n") }
        }

        markwon.setMarkdown(terms_view, termsString.toString())

        view_online.setOnClickListener {
            isAtBottom = true
            context.launchUrl("https://github.com/zacharee/Tweaker/blob/master/app/src/main/assets/terms.md")
        }

        terms_holder.setOnScrollChangeListener { _, _, _, _, _ ->
            if (!terms_holder.canScrollVertically(1)) {
                isAtBottom = true
            }
        }
    }

    fun canGoForward(): Boolean {
        return isAtBottom
    }
}