package com.shajt.caffshop.viewmodels.home

import androidx.lifecycle.ViewModel
import com.shajt.caffshop.data.CaffShopRepository

class HomeViewModel(
    private val caffShopRepository: CaffShopRepository
) : ViewModel() {

    val userIsAdmin: Boolean
        get(){
            return caffShopRepository.user?.isAdmin!!
        }
}