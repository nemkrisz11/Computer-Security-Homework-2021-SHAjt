package com.shajt.caffshop.data.models

/**
 * Comment list representation with total page number.
 */
data class CommentList(
    val comments: List<Comment>,
    val totalPages: Int
)