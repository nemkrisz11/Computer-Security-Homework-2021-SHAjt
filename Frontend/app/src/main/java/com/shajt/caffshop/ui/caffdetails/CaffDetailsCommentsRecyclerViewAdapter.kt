package com.shajt.caffshop.ui.caffdetails

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.shajt.caffshop.data.models.Comment
import com.shajt.caffshop.databinding.FragmentCaffDetailsCommentListItemBinding
import com.shajt.caffshop.ui.commons.CaffRecyclerViewAdapter
import java.text.SimpleDateFormat
import java.util.*

class CaffDetailsCommentsRecyclerViewAdapter(
    private val isAdmin: Boolean = false,
    private val deleteComment: (comment: Comment) -> Unit
) :
    CaffRecyclerViewAdapter<Comment, CaffDetailsCommentsRecyclerViewAdapter.CommentListItemViewHolder>(
        CommentEntityCallback()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentListItemViewHolder {
        return CommentListItemViewHolder(
            FragmentCaffDetailsCommentListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CommentListItemViewHolder, position: Int) {
        val item = getItem(position)

        holder.username.text = item.username
        holder.date.text = SimpleDateFormat.getDateInstance().format(Date(item.date))
        holder.comment.text = item.comment
        if (isAdmin) {
            holder.delete.setOnClickListener {
                deleteComment(item)
            }
        }
    }


    inner class CommentListItemViewHolder(binding: FragmentCaffDetailsCommentListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val username = binding.username
        val date = binding.date
        val comment = binding.comment
        val delete = binding.delete
    }

    private class CommentEntityCallback : DiffUtil.ItemCallback<Comment>() {

        override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem == newItem
        }
    }
}