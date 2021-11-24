package com.shajt.caffshop.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shajt.caffshop.data.CaffShopRepository
import com.shajt.caffshop.viewmodels.auth.AuthViewModel
import com.shajt.caffshop.viewmodels.home.HomeViewModel
import com.shajt.caffshop.viewmodels.user.DetailedUserViewModel

/**
 * ViewModel provider factory to instantiate a view model.
 */
class CaffShopViewModelFactory(
    private val caffShopRepository: CaffShopRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(caffShopRepository) as T
        } else if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(caffShopRepository) as T
        } else if (modelClass.isAssignableFrom(DetailedUserViewModel::class.java)) {
            return DetailedUserViewModel(caffShopRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}