package com.shajt.caffshop.data.models.auth

import com.shajt.caffshop.data.enums.ErrorMessage

/**
 * Data validation state of the auth form.
 */
data class AuthFormState(
    val usernameError: ErrorMessage? = null,
    val passwordError: ErrorMessage? = null,
    val passwordAgainError: ErrorMessage? = null,
    val isDataValid: Boolean = false
)