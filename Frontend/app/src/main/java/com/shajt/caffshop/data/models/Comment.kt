package com.shajt.caffshop.data.models

data class Comment(
    val id: Int,
    val caffId: Int,
    val username: String,
    val comment: String,
    val date: Long
)
