package com.shajt.caffshop.ui.commons

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

abstract class CaffRecyclerViewAdapter<T, VH : RecyclerView.ViewHolder>(
    itemCallback: DiffUtil.ItemCallback<T>
) : ListAdapter<T, VH>(itemCallback)