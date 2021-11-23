package com.shajt.caffshop.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import androidx.core.content.edit
import com.shajt.caffshop.data.enums.ErrorMessage
import com.shajt.caffshop.data.models.ServerResult
import com.shajt.caffshop.data.models.auth.AuthResult
import com.shajt.caffshop.data.models.User
import com.shajt.caffshop.data.models.UserData
import com.shajt.caffshop.data.models.auth.LoginResult
import com.shajt.caffshop.data.models.auth.UserCredentials
import com.shajt.caffshop.network.CaffShopApiInteractor
import com.shajt.caffshop.utils.DeCryptor
import com.shajt.caffshop.utils.EnCryptor

class CaffShopRepository(
    private val apiInteractor: CaffShopApiInteractor,
    baseContext: Context
) {

    companion object {
        const val ENCRYPTION_ALIAS = "caffShopUserToken"
    }

    private val enCryptor = EnCryptor()
    private val deCryptor = DeCryptor()

    private val sharedPreferences: SharedPreferences =
        baseContext.getSharedPreferences("caffShopPrefs", Context.MODE_PRIVATE)

    var user: User? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        user = loadUser()
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
        if (check != null) {
            return AuthResult(error = check)
        }
        val userData = userDataServerResult.result!!

        /* // For testing
        val userData = UserData(userCredentials.username, false, 1000)
        val loginResult = LoginResult("abcd", 1000)
         */

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

    private fun loadUser(): User? {
        with(sharedPreferences) {
            val username = getString("username", null)
            val encryptedTokenText = getString("encryptedToken", null)
            val expire = getLong("expire", -1L)
            val regDate = getLong("regDate", -1L)
            val isAdmin = getBoolean("isAdmin", false)
            val ivText = getString("iv", null)

            if (username == null || encryptedTokenText == null || expire == -1L || regDate == -1L || ivText == null) {
                return null
            }

            val encryptedToken = Base64.decode(encryptedTokenText, Base64.DEFAULT)
            val iv = Base64.decode(ivText, Base64.DEFAULT)
            val token = deCryptor.decryptData(ENCRYPTION_ALIAS, encryptedToken, iv)

            return User(username, token, expire, regDate, isAdmin)
        }
    }

    private fun setUser(user: User) {
        this.user = user
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore

        val encryptedToken = enCryptor.encryptText(ENCRYPTION_ALIAS, user.token)
        val encryptedTokenText = Base64.encodeToString(encryptedToken, Base64.DEFAULT)
        val ivText = Base64.encodeToString(enCryptor.iv, Base64.DEFAULT)

        sharedPreferences.edit(commit = true) {
            putString("username", user.username)
            putString("encryptedToken", encryptedTokenText)
            putLong("expire", user.expire)
            putLong("regDate", user.regDate)
            putBoolean("isAdmin", user.isAdmin)
            putString("iv", ivText)
        }
    }

    fun logout() {
        user = null
        sharedPreferences.edit(commit = true) {
            clear()
        }
    }
}