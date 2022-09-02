package com.zacharee1.systemuituner.util

import android.animation.LayoutTransition
import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.zacharee1.systemuituner.R
import kotlin.math.roundToInt

fun View.scaleAnimatedVisible(visible: Boolean, listener: Animation.AnimationListener? = null) {
    val anim =
        AnimationUtils.loadAnimation(context, if (visible) R.anim.scale_in else R.anim.scale_out)
    anim.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationRepeat(animation: Animation?) {
            listener?.onAnimationRepeat(animation)
        }

        override fun onAnimationStart(animation: Animation?) {
            listener?.onAnimationStart(animation)
        }

        override fun onAnimationEnd(animation: Animation?) {
            if (!visible) {
                isVisible = false
                alpha = 0f
            } else {
                alpha = 1f
            }
            listener?.onAnimationEnd(animation)
        }
    })
    if (visible) {
        isVisible = true
        alpha = 0f
    } else {
        alpha = 1f
    }
    startAnimation(anim)
}

var View.scaleAnimatedVisible: Boolean
    get() = isVisible
    set(value) {
        scaleAnimatedVisible(value)
    }

fun ViewGroup.invalidateRecursive() {
    val count = childCount
    var child: View
    for (i in 0 until count) {
        child = getChildAt(i)
        if (child is ViewGroup) child.invalidateRecursive() else child.invalidate()
    }
}

fun ViewGroup.addAnimation() {
    layoutTransition = LayoutTransition().apply {
        this.enableTransitionType(LayoutTransition.CHANGING)
    }
}

fun Context.dpAsPx(dpVal: Number) =
    TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dpVal.toFloat(),
        resources.displayMetrics
    ).roundToInt()

fun Context.spAsPx(spVal: Number) =
    TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        spVal.toFloat(),
        resources.displayMetrics
    ).roundToInt()

fun Context.asDp(value: Number) =
    value.toFloat() / resources.displayMetrics.density

fun View.visibilityChanged(owner: LifecycleOwner, action: (View) -> Unit) {
    val listener = ViewTreeObserver.OnGlobalLayoutListener {
        val newVis: Int = this.visibility
        if (this.tag as Int? != newVis) {
            this.tag = this.visibility

            // visibility has changed
            action(this)
        }
    }

    owner.lifecycle.addObserver(object : DefaultLifecycleObserver {
        override fun onResume(owner: LifecycleOwner) {
            viewTreeObserver.addOnGlobalLayoutListener(listener)
        }

        override fun onPause(owner: LifecycleOwner) {
            viewTreeObserver.removeOnGlobalLayoutListener(listener)
        }
    })
}
