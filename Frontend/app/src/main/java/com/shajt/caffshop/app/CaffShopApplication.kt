package com.shajt.caffshop.app

import android.app.Application
import com.shajt.caffshop.data.CaffShopRepository
import com.shajt.caffshop.network.CaffShopApiInteractor
import com.shajt.caffshop.viewmodels.CaffShopViewModelFactory

class CaffShopApplication : Application() {

    val caffShopApiInteractor by lazy { CaffShopApiInteractor() }
    val caffShopRepository by lazy { CaffShopRepository(caffShopApiInteractor, baseContext) }
    val caffShopViewModelFactory by lazy { CaffShopViewModelFactory(caffShopRepository) }

}