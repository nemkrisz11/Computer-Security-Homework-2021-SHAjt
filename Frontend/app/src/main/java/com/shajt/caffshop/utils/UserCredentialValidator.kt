package com.shajt.caffshop.utils

import android.util.Patterns

object UserCredentialValidator {

    fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    fun isPasswordValid(password: String): Boolean {
        return password.length > 8
    }
}