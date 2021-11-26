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

    val userIsAdmin: Boolean
        get() = caffShopRepository.localUser?.isAdmin!!

    val currentUsername: String
        get() = caffShopRepository.localUser?.username!!

    fun getUserDetails(username: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if(username.isBlank())
            {
                val localUser = UserData(
                    username = caffShopRepository.localUser?.username!!,
                    isAdmin = caffShopRepository.localUser?.isAdmin!!,
                    regDate = caffShopRepository.localUser?.regDate!!
                )

                _user.postValue(localUser)
            }
            val userResult = caffShopRepository.getUser(username)
            if (userResult.success != null) {
                _user.postValue(userResult.success!!)
            } else {
                _error.postValue(userResult.error!!)
            }
        }
    }

    fun changePassword(newPassword: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val modifyPasswordResult = caffShopRepository.modifyPassword(ModifyPassword(user.value?.username!!, newPassword))
            if (!modifyPasswordResult.success) {
                _error.postValue(modifyPasswordResult.error!!)
            }
        }
    }
}