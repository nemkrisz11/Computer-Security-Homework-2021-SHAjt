package com.shajt.caffshop.viewmodels.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import androidx.lifecycle.viewModelScope

import com.shajt.caffshop.data.CaffShopRepository
import com.shajt.caffshop.data.models.Error
import com.shajt.caffshop.data.models.auth.UserCredentials
import com.shajt.caffshop.data.models.auth.AuthFormState
import com.shajt.caffshop.data.models.auth.AuthResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthViewModel(
    private val caffShopRepository: CaffShopRepository
) : ViewModel() {

    private val _authForm = MutableLiveData<AuthFormState>()
    val authFormState: LiveData<AuthFormState> = _authForm

    private val _authResult = MutableLiveData<AuthResult>()
    val authResult: LiveData<AuthResult> = _authResult

    fun login(username: String, password: String) {
        // Log in or register user in the background and post result
        viewModelScope.launch(Dispatchers.IO) {
            val result = caffShopRepository.loginOrRegister(UserCredentials(username, password))
            _authResult.postValue(result)
        }
    }

    /**
     * Validates typed data.
     */
    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _authForm.value = AuthFormState(usernameError = Error.INVALID_USERNAME)
        } else if (!isPasswordValid(password)) {
            _authForm.value = AuthFormState(passwordError = Error.INVALID_PASSWORD)
        } else {
            _authForm.value = AuthFormState(isDataValid = true)
        }
    }

    // TODO change this placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // TODO changes this placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 8
    }
}