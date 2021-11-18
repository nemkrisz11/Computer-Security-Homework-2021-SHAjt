package com.shajt.caffshop.data.models

/**
 * User representation.
 */
data class User(
    val username: String,
    val sessionId: String,
    val isAdmin: Boolean = false
)