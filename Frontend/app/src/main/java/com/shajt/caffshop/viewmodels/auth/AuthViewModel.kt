package com.shajt.caffshop.viewmodels.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import androidx.lifecycle.viewModelScope

import com.shajt.caffshop.data.CaffShopRepository
import com.shajt.caffshop.data.enums.ErrorMessage
import com.shajt.caffshop.data.models.auth.UserCredentials
import com.shajt.caffshop.data.models.auth.AuthFormState
import com.shajt.caffshop.data.models.auth.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val caffShopRepository: CaffShopRepository
) : ViewModel() {

    private val _authForm = MutableLiveData<AuthFormState>()
    val authFormState: LiveData<AuthFormState> = _authForm

    private val _authResult = MutableLiveData<AuthResult>()
    val authResult: LiveData<AuthResult> = _authResult

    fun login(username: String, password: String) {
        // Log in user in the background and post result
        viewModelScope.launch(Dispatchers.IO) {
            val result = caffShopRepository.login(UserCredentials(username, password))
            _authResult.postValue(result)
        }
    }

    fun register(username: String, password: String) {
        // Register user in the background and post result
        viewModelScope.launch(Dispatchers.IO) {
            val result = caffShopRepository.register(UserCredentials(username, password))
            _authResult.postValue(result)
        }
    }

    /**
     * Validates typed data.
     */
    fun authDataChanged(username: String, password: String, passwordAgain: String?) {
        if (!isUserNameValid(username)) {
            _authForm.value = AuthFormState(usernameError = ErrorMessage.INVALID_USERNAME)
        } else if (!isPasswordValid(password)) {
            _authForm.value = AuthFormState(passwordError = ErrorMessage.INVALID_PASSWORD)
        } else if (passwordAgain != null && password != passwordAgain) {
            _authForm.value = AuthFormState(passwordAgainError = ErrorMessage.INVALID_PASSWORD_AGAIN)
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