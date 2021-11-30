package com.shajt.caffshop.network

import com.shajt.caffshop.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.HttpUrl.Companion.toHttpUrl
import javax.inject.Singleton

/**
 * Module for dependency injection.
 */
@Module
@InstallIn(SingletonComponent::class)
object CaffShopApiModule {

    @Singleton
    @Provides
    fun provideCaffShopApiInteractor(): CaffShopApiInteractor {
        return CaffShopApiInteractor(BuildConfig.BACKEND_URL.toHttpUrl())
    }
}