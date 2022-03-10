package com.example.pinterest.ui.fragments.helper

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class SpaceItemDecoration(private val space: Int) : ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.left = space
        outRect.bottom = space
        outRect.right = space
        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.top = space
        }
    }
}