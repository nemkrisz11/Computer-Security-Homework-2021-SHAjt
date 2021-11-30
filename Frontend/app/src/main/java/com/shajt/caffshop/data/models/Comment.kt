package com.shajt.caffshop.data.models

/**
 * Comment representation.
 */
data class Comment(
    val id: Int,
    val caffId: String,
    val username: String,
    val comment: String,
    val date: Long
)
