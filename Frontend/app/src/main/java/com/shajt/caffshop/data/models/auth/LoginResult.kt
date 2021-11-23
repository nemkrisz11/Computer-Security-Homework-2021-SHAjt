package com.shajt.caffshop.data.models.auth

data class LoginResult(
    val token: String,
    val expire: Long
)