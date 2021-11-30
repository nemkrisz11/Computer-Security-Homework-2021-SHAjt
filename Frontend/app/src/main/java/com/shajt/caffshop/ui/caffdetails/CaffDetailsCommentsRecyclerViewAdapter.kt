package com.shajt.caffshop.ui.caffdetails

import android.animation.ValueAnimator
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.doOnLayout
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.shajt.caffshop.data.models.Comment
import com.shajt.caffshop.databinding.FragmentCaffDetailsCommentListItemBinding
import com.shajt.caffshop.ui.commons.GenericRecyclerViewAdapter
import java.text.SimpleDateFormat
import java.util.*

class CaffDetailsCommentsRecyclerViewAdapter(
    private val isAdmin: Boolean = false,
    private val deleteComment: (comment: Comment) -> Unit
) : GenericRecyclerViewAdapter<Comment, CaffDetailsCommentsRecyclerViewAdapter.CommentListItemViewHolder>() {

    private var originalHeight = -1
    private var expandedHeight = -1

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
        holder.delete.isVisible = false
        if (isAdmin) {
            holder.expand.isVisible = true
            holder.root.setOnClickListener {
                with(holder.delete) {
                    if (isVisible) {
                        resize(holder, false)
                    } else {
                        resize(holder, true)
                    }
                    setOnClickListener {
                        resize(holder, false)
                        deleteComment(item)
                    }
                }
            }
        }
    }

    override fun onViewAttachedToWindow(holder: CommentListItemViewHolder) {
        super.onViewAttachedToWindow(holder)

        if (expandedHeight < 0) {
            holder.root.doOnLayout { view ->
                originalHeight = view.height
                holder.delete.isVisible = true

                view.doOnPreDraw {
                    // TODO find out why predraw delete height is zero
                    holder.delete.height
                    expandedHeight = view.height * 2 // TODO remove *2 if perv is solved
                    holder.delete.isVisible = false
                }
            }
        }
    }

    private fun resize(holder: CommentListItemViewHolder, expand: Boolean) {
        val animator = if (expand) {
            ValueAnimator.ofFloat(0F, 1F)
        } else {
            ValueAnimator.ofFloat(1F, 0F)
        }
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.addUpdateListener {
            val progress = it.animatedValue as Float
            holder.root.layoutParams.height =
                (originalHeight + (expandedHeight - originalHeight) * progress).toInt()
            holder.root.requestLayout()

            holder.expand.rotation = 180 * progress
        }
        if (expand) {
            animator.doOnStart {
                holder.delete.isVisible = true
                holder.setIsRecyclable(false)
            }
        } else {
            animator.doOnEnd {
                holder.delete.isVisible = false
                holder.setIsRecyclable(true)
            }
        }
        animator.start()
    }


    inner class CommentListItemViewHolder(binding: FragmentCaffDetailsCommentListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val root = binding.root
        val username = binding.username
        val date = binding.date
        val comment = binding.comment
        val expand = binding.expand
        val delete = binding.delete
    }
}