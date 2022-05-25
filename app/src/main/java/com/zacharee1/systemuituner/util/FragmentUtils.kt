package com.zacharee1.systemuituner.util

import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlin.math.floor

fun Fragment.chooseLayoutManagerWithoutSetting(
    view: View?,
    grid: RecyclerView.LayoutManager,
    linear: RecyclerView.LayoutManager,
    extraFlags: Boolean = true,
): RecyclerView.LayoutManager {
    val dpWidth = requireContext().asDp(view?.width ?: 0)

    return if (extraFlags && dpWidth >= 800) {
        grid
    } else {
        linear
    }
}

fun Fragment.updateLayoutManager(
    view: View?,
    recycler: RecyclerView?,
    grid: RecyclerView.LayoutManager,
    linear: RecyclerView.LayoutManager,
    extraFlags: Boolean = true
) {
    recycler?.layoutManager = chooseLayoutManager(view, grid, linear, extraFlags)
}

fun Fragment.chooseLayoutManager(
    view: View?,
    grid: RecyclerView.LayoutManager,
    linear: RecyclerView.LayoutManager,
    extraFlags: Boolean = true,
    spanCount: (Float) -> Int = {
        floor(it / 400).toInt()
    }
): RecyclerView.LayoutManager {
    val dpWidth = requireContext().asDp(view?.width ?: 0)

    return if (extraFlags && dpWidth >= 800) {
        grid.also {
            if (it is GridLayoutManager) {
                it.spanCount = spanCount(dpWidth)
            }

            if (it is StaggeredGridLayoutManager) {
                it.spanCount = spanCount(dpWidth)
            }
        }
    } else {
        linear
    }
}

fun Fragment.updateTitle(title: Int) {
    activity?.setTitle(title)
}

fun Fragment.updateTitle(title: CharSequence?) {
    activity?.title = title
}