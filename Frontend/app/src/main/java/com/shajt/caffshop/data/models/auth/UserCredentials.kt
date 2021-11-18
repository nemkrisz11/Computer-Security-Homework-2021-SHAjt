package com.shajt.caffshop.data.models.auth

/**
 * User credentials used for registration or login.
 */
data class UserCredentials(
    val username: String,
    val password: String
)