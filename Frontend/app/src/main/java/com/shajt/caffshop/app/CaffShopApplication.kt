package com.shajt.caffshop.app

import android.app.Application
import com.shajt.caffshop.data.CaffShopRepository
import com.shajt.caffshop.viewmodels.CaffShopViewModelFactory

class CaffShopApplication : Application() {

    // TODO add api
    val caffShopRepository by lazy { CaffShopRepository() }
    val caffShopViewModelFactory by lazy { CaffShopViewModelFactory(caffShopRepository) }

}