package com.shajt.caffshop.ui.commons

import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

/**
 * Generic recycler view adapter.
 */
abstract class GenericRecyclerViewAdapter<T, VH : RecyclerView.ViewHolder> :
    ListAdapter<T, VH>(GenericDataClassItemCallback<T>())