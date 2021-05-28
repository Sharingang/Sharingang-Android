package com.example.sharingang.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewDecorator {

    /**
     * Decorates the displayed list of users with a margin between elements.
     *
     * @param margin the margin between items
     * @param recyclerView the RecyclerView to decorate
     */
    fun setRecyclerViewDecoration(margin: Int, recyclerView: RecyclerView) {
        recyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State,
            ) {
                super.getItemOffsets(outRect, view, parent, state)
                if (parent.getChildAdapterPosition(view) > 0) {
                    outRect.top = margin
                }
            }
        })
    }
}