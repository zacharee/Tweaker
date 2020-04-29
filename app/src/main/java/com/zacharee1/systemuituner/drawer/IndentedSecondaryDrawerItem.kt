package com.zacharee1.systemuituner.drawer

import com.mikepenz.materialdrawer.holder.DimenHolder
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem
import com.zacharee1.systemuituner.util.SecondaryStyle

class IndentedSecondaryDrawerItem : SecondaryDrawerItem() {
    init {
        badgeStyle = SecondaryStyle()
    }

    override fun bindView(holder: ViewHolder, payloads: List<Any>) {
        super.bindView(holder, payloads)

        holder.itemView.apply {
            val start = DimenHolder.fromDp(32).asPixel(context)
            setPadding(start, paddingTop, paddingRight, paddingBottom)
            setPaddingRelative(start, paddingTop, paddingEnd, paddingBottom)
        }
    }
}