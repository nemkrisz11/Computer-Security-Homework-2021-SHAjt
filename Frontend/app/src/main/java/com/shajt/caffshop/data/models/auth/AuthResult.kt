package com.shajt.caffshop.data.models.auth

import com.shajt.caffshop.data.models.Error
import com.shajt.caffshop.data.models.User

/**
 * Authentication result : success (user details) or error.
 */
data class AuthResult(
    val success: User? = null,
    val error: Error? = null
)