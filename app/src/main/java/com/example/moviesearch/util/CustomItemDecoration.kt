package com.example.moviesearch.util

import android.graphics.Rect
import androidx.recyclerview.widget.RecyclerView

class CustomItemDecoration(private val size: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, itemPosition: Int, parent: RecyclerView) {
        outRect.right = size
        outRect.left = size
    }
}