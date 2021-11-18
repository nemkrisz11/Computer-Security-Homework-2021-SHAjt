package com.shajt.caffshop.data

import com.shajt.caffshop.data.models.Error
import com.shajt.caffshop.data.models.auth.AuthResult
import com.shajt.caffshop.data.models.User
import com.shajt.caffshop.data.models.auth.UserCredentials

class CaffShopRepository(
    // TODO add api
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

    fun loginOrRegister(userCredentials: UserCredentials): AuthResult {
        if (!validateCredentials(userCredentials)) {
            return AuthResult(error = Error.REQUIRED_FIELD_IS_EMPTY)
        }

        // TODO call api

        val resultUser = User(userCredentials.username, "")
        setUser(resultUser)
        return AuthResult(success = resultUser) // TODO modify to api result
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