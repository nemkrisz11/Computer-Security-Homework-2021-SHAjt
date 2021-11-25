package com.shajt.caffshop.viewmodels.home

import androidx.lifecycle.ViewModel
import com.shajt.caffshop.data.CaffShopRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val caffShopRepository: CaffShopRepository
) : ViewModel() {

    val userIsAdmin: Boolean
        get(){
            return caffShopRepository.localUser?.isAdmin!!
        }
}