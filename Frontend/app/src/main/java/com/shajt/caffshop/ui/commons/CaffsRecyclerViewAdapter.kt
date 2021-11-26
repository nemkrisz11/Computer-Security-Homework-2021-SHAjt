package com.shajt.caffshop.ui.commons

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shajt.caffshop.data.models.Caff
import com.shajt.caffshop.databinding.ViewCaffListItemBinding

class CaffsRecyclerViewAdapter(
    private val selected: (caffId: Int) -> Unit
) : GenericRecyclerViewAdapter<Caff, CaffsRecyclerViewAdapter.CaffsListItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CaffsListItemViewHolder {
        return CaffsListItemViewHolder(
            ViewCaffListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CaffsListItemViewHolder, position: Int) {
        val item = getItem(position)

        holder.root.setOnClickListener {
            selected(item.id)
        }
        holder.name.text = item.caffName
    }

    inner class CaffsListItemViewHolder(binding: ViewCaffListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val root = binding.root
        val ciff = binding.ciff
        val name = binding.name
    }
}