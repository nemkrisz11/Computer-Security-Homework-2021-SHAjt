package com.shajt.caffshop.data

import com.shajt.caffshop.data.enums.ErrorMessage
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

        val result = apiInteractor.login(userCredentials)

        if (result.success != null) {
            setUser(result.success)
        }
        return result
    }

    suspend fun register(userCredentials: UserCredentials): AuthResult {
        if (!validateCredentials(userCredentials)) {
            return AuthResult(error = ErrorMessage.REQUIRED_FIELD_IS_EMPTY)
        }

        val result = apiInteractor.register(userCredentials)

        if (result.success != null) {
            setUser(result.success)
        }
        return result
    }

    // TODO find a better name
    private fun validateCredentials(cred: UserCredentials): Boolean {
        return !(cred.username.isBlank() || cred.password.isBlank())
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