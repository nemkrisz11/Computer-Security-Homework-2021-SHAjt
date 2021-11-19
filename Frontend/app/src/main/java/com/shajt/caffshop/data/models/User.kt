package com.shajt.caffshop.data.models

/**
 * User representation.
 */
data class User(
    val username: String,
    val token: String,
    val isAdmin: Boolean = false
)