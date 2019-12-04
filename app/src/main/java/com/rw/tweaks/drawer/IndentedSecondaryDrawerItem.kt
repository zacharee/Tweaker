package com.rw.tweaks.drawer

import com.mikepenz.materialdrawer.model.SecondaryDrawerItem
import com.mikepenz.materialize.holder.DimenHolder
import com.rw.tweaks.util.SecondaryStyle

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