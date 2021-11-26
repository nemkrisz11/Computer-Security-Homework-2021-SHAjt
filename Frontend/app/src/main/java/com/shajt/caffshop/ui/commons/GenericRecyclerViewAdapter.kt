package com.shajt.caffshop.ui.commons

import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

abstract class GenericRecyclerViewAdapter<T, VH : RecyclerView.ViewHolder> :
    ListAdapter<T, VH>(GenericDataClassItemCallback<T>())