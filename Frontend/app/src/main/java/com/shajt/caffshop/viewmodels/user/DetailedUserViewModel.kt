package com.shajt.caffshop.viewmodels.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shajt.caffshop.data.CaffShopRepository
import com.shajt.caffshop.data.enums.ErrorMessage
import com.shajt.caffshop.data.models.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailedUserViewModel @Inject constructor(
    private val caffShopRepository: CaffShopRepository
) : ViewModel() {

    private var _error = MutableLiveData<ErrorMessage>()
    val error: LiveData<ErrorMessage> = _error

    private var _user = MutableLiveData<UserData>()
    val user: LiveData<UserData> = _user

    private var _deleteUserResult = MutableLiveData<Boolean>()
    val deleteUserResult: LiveData<Boolean> = _deleteUserResult

    private var _modifyPasswordResult = MutableLiveData<Boolean>()
    val modifyPasswordResult: LiveData<Boolean> = _modifyPasswordResult

    val userIsAdmin: Boolean
        get() = caffShopRepository.localUser?.isAdmin!!

    val currentUsername: String
        get() = caffShopRepository.localUser?.username!!

    /**
     * Requests for user details.
     *
     * @param username username
     */
    fun getUserDetails(username: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (username.isBlank()) {
                val localUser = UserData(
                    username = caffShopRepository.localUser?.username!!,
                    isAdmin = caffShopRepository.localUser?.isAdmin!!,
                    regDate = caffShopRepository.localUser?.regDate!!
                )

                _user.postValue(localUser)
                return@launch
            }
            val userResult = caffShopRepository.getUser(username)
            if (userResult.success != null) {
                _user.postValue(userResult.success!!)
            } else {
                _error.postValue(userResult.error!!)
            }
        }
    }

    /**
     * Deletes user.
     *
     * @param username username
     */
    fun deleteUser(username: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val deleteUserResult = caffShopRepository.deleteUser(username)
            if (deleteUserResult.success) {
                _deleteUserResult.postValue(true)
            } else {
                _error.postValue(deleteUserResult.error!!)
            }
        }
    }

    /**
     * Changes user password.
     *
     * @param newPassword new password
     */
    fun changePassword(newPassword: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val modifyPasswordResult = caffShopRepository.modifyPassword(
                ModifyPassword(
                    if (currentUsername == user.value?.username!!) {
                        null
                    } else {
                        user.value?.username
                    },
                    newPassword
                )
            )
            if (modifyPasswordResult.success) {
                _modifyPasswordResult.postValue(true)
            } else {
                _error.postValue(modifyPasswordResult.error!!)
            }
        }
    }
}