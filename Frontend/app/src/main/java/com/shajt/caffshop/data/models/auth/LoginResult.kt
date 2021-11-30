package com.shajt.caffshop.data.models.auth

/**
 * Login result : token and expire date.
 */
data class LoginResult(
    val token: String,
    val expire: Long
)