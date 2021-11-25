package com.shajt.caffshop.viewmodels.user

import androidx.lifecycle.ViewModel
import com.shajt.caffshop.data.CaffShopRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailedUserViewModel @Inject constructor(
    private val caffShopRepository: CaffShopRepository
) : ViewModel() {
    // TODO: Implement the ViewModel
}