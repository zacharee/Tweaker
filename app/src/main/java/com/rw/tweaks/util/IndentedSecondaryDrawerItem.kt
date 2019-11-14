package com.rw.tweaks.util

import com.mikepenz.materialdrawer.model.SecondaryDrawerItem
import com.mikepenz.materialize.holder.DimenHolder

class IndentedSecondaryDrawerItem : SecondaryDrawerItem() {
    init {
        withBadgeStyle(SecondaryStyle())
    }

    override fun bindView(holder: ViewHolder, payloads: MutableList<Any>) {
        super.bindView(holder, payloads)

        holder.itemView.apply {
            val start = DimenHolder.fromDp(32).asPixel(context)
            setPadding(start, paddingTop, paddingRight, paddingBottom)
            setPaddingRelative(start, paddingTop, paddingEnd, paddingBottom)
        }
    }
}