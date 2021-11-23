package com.shajt.caffshop.data

import com.shajt.caffshop.data.enums.ErrorMessage
import com.shajt.caffshop.data.models.ServerResult
import com.shajt.caffshop.data.models.auth.AuthResult
import com.shajt.caffshop.data.models.User
import com.shajt.caffshop.data.models.auth.UserCredentials
import com.shajt.caffshop.network.CaffShopApiInteractor

class CaffShopRepository(
    private val apiInteractor: CaffShopApiInteractor
) {

    var user: User? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null

    init {
        // TODO load user form cache
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        user = null
    }

    suspend fun login(userCredentials: UserCredentials): AuthResult {
        if (!validateCredentials(userCredentials)) {
            return AuthResult(error = ErrorMessage.REQUIRED_FIELD_IS_EMPTY)
        }

        val loginServerResult = apiInteractor.login(userCredentials)
        var check = checkServerResult(loginServerResult, ErrorMessage.LOGIN_FAILED)
        if (check != null) {
            return AuthResult(error = check)
        }
        val loginResult = loginServerResult.result!!

        val userDataServerResult = apiInteractor.getUser(loginResult.token, userCredentials.username)
        check = checkServerResult(userDataServerResult, ErrorMessage.LOGIN_FAILED)
        val userData = userDataServerResult.result!!

        val user = User(
            userData.username,
            loginResult.token,
            loginResult.expire,
            userData.regDate,
            userData.isAdmin
        )
        setUser(user)
        return AuthResult(success = user)
    }

    suspend fun register(userCredentials: UserCredentials): AuthResult {
        if (!validateCredentials(userCredentials)) {
            return AuthResult(error = ErrorMessage.REQUIRED_FIELD_IS_EMPTY)
        }

        val registrationResult = apiInteractor.register(userCredentials)
        val check = checkServerResult(registrationResult, ErrorMessage.REGISTRATION_FAILED)
        if (check != null) {
            return AuthResult(error = check)
        }

        return login(userCredentials)
    }

    // TODO find a better name
    private fun validateCredentials(cred: UserCredentials): Boolean {
        return !(cred.username.isBlank() || cred.password.isBlank())
    }

    private fun <R> checkServerResult(
        serverResult: ServerResult<R, ErrorMessage>,
        baseError: ErrorMessage
    ): ErrorMessage? {
        return when {
            serverResult.error != null -> serverResult.error
            serverResult.result == null -> baseError
            else -> null
        }
    }

    private fun setUser(user: User) {
        this.user = user
        // TODO cache
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }

    fun logout() {
        user = null
        // TODO remove cache
    }
}