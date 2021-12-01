package com.shajt.caffshop.utils

import android.util.Patterns

/**
 * User credentials validator.
 */
object UserCredentialValidator {

    /**
     * Validates username.
     */
    fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    /**
     * Validates password.
     */
    fun isPasswordValid(password: String): Boolean {
        return password.length > 8
    }
}