package com.shajt.caffshop.viewmodels.users

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shajt.caffshop.data.CaffShopRepository
import com.shajt.caffshop.data.enums.ErrorMessage
import com.shajt.caffshop.data.models.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsersViewModel @Inject constructor(
    private val caffShopRepository: CaffShopRepository
) : ViewModel() {

    private var _error = MutableLiveData<ErrorMessage>()
    val error: LiveData<ErrorMessage> = _error

    private var actualPage = 0
    private var totalPages = 1

    private var _users = MutableLiveData<List<UserData>>()
    val users: LiveData<List<UserData>> = _users

    /**
     * Requests for users.
     */
    fun getMoreUsers() {
        if (actualPage >= totalPages) {
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            val userList = caffShopRepository.getUsers(actualPage + 1)
            if (userList.success != null) {
                actualPage++
                totalPages = userList.success.totalPages
                if (users.value != null) {
                    val mergedList = _users.value!!.toMutableList().apply {
                        addAll(userList.success.users)
                    }
                    _users.postValue(mergedList)
                } else {
                    _users.postValue(userList.success.users)
                }
            } else {
                _error.postValue(userList.error!!)
            }
        }
    }

}