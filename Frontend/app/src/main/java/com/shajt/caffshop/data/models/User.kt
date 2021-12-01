package com.shajt.caffshop.data.models

/**
 * User representation.
 */
data class User(
    val username: String,
    val token: String,
    val expire: Long,
    val regDate: Long,
    val isAdmin: Boolean = false
)