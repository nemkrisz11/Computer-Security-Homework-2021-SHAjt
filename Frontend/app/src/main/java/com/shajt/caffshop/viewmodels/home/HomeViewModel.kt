package com.shajt.caffshop.viewmodels.home

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shajt.caffshop.data.CaffShopRepository
import com.shajt.caffshop.data.enums.ErrorMessage
import com.shajt.caffshop.data.models.Caff
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val caffShopRepository: CaffShopRepository
) : ViewModel() {

    private var _error = MutableLiveData<ErrorMessage>()
    val error: LiveData<ErrorMessage> = _error

    private var _caffs = MutableLiveData<List<Caff>>()
    val caffs: LiveData<List<Caff>> = _caffs

    private var _uploadSuccess = MutableLiveData<Boolean>()
    val uploadSuccess: LiveData<Boolean> = _uploadSuccess

    private var actualPage = 0
    private var totalPages = 1

    val userIsAdmin: Boolean
        get() = caffShopRepository.localUser?.isAdmin!!

    init {
        getMoreCaffs()
    }

    fun getMoreCaffs() {
        if (actualPage >= totalPages) {
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            val searchCaffResult = caffShopRepository.searchCaffs(page = actualPage + 1)
            if (searchCaffResult.success != null) {
                actualPage++
                totalPages = searchCaffResult.success.totalPages
                if (caffs.value != null) {
                    val mergedList = _caffs.value!!.toMutableList().apply {
                        addAll(searchCaffResult.success.caffs)
                    }
                    _caffs.postValue(mergedList)
                } else {
                    _caffs.postValue(searchCaffResult.success.caffs)
                }
            } else {
                _error.postValue(searchCaffResult.error!!)
            }
        }
    }

    fun uploadCaff(uri: Uri, name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val uploadCaffResult = caffShopRepository.uploadCaff(uri, name)
            if (uploadCaffResult.success) {
                _uploadSuccess.postValue(true)
            } else {
                _error.postValue(uploadCaffResult.error!!)
            }
        }
    }

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            caffShopRepository.logout()
        }
    }
}