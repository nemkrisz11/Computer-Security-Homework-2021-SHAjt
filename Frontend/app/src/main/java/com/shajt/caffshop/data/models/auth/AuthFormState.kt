package com.shajt.caffshop.data.models.auth

import com.shajt.caffshop.data.models.Error

/**
 * Data validation state of the auth form.
 */
data class AuthFormState(
    val usernameError: Error? = null,
    val passwordError: Error? = null,
    val passwordAgainError: Error? = null,
    val isDataValid: Boolean = false
)