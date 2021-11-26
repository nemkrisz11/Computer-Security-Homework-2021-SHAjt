package com.shajt.caffshop.ui.commons

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class GenericSwipeCallback(
    val action: (position: Int) -> Unit,
    directions: Int = ItemTouchHelper.LEFT
) : ItemTouchHelper.SimpleCallback(0, directions) {

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        action(viewHolder.adapterPosition)
    }
}