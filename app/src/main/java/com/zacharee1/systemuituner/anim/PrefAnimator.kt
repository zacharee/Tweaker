package com.zacharee1.systemuituner.anim

import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import jp.wasabeef.recyclerview.animators.BaseItemAnimator

class PrefAnimator : BaseItemAnimator() {
    override fun animateRemoveImpl(holder: RecyclerView.ViewHolder) {
        holder.itemView.animate()
            .alpha(0f)
            .scaleX(0.95f)
            .scaleY(0.95f)
            .setDuration(removeDuration)
            .setInterpolator(interpolator)
            .setListener(DefaultRemoveAnimatorListener(holder))
            .setStartDelay(getRemoveDelay(holder))
            .start()
    }

    override fun preAnimateAddImpl(holder: RecyclerView.ViewHolder) {
        holder.itemView.alpha = 0f
        holder.itemView.scaleX = 0.95f
        holder.itemView.scaleY = 0.95f
    }

    override fun animateAddImpl(holder: RecyclerView.ViewHolder) {
        holder.itemView.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(addDuration)
            .setInterpolator(interpolator)
            .setListener(DefaultAddAnimatorListener(holder))
            .setStartDelay(getAddDelay(holder))
            .start()
    }
}