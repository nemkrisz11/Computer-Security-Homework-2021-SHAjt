package com.shajt.caffshop.network

import com.shajt.caffshop.data.models.Error
import com.shajt.caffshop.data.models.User
import com.shajt.caffshop.data.models.auth.AuthResult
import com.shajt.caffshop.data.models.auth.UserCredentials
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CaffShopApiInteractor(
    private val baseUrl: HttpUrl = CaffShopApi.BASE_URL.toHttpUrl()
) {

    private val caffShopApi: CaffShopApi

    init {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val httpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()

        this.caffShopApi = retrofit.create(CaffShopApi::class.java)
    }

    suspend fun login(userCredentials: UserCredentials): AuthResult {
        return try {
            val result = caffShopApi.login(userCredentials)
            return if (result.isSuccessful && result.body() != null) {
                // TODO
                val token = result.body()!!["token"].asString
                val isAdmin = result.body()!!["isAdmin"].asBoolean
                AuthResult(success = User(userCredentials.username, token, isAdmin))
            } else {
                // TODO send specific error based on the response error code
                AuthResult(error = Error.AUTH_FAILED)
            }
        } catch (e: Exception) {
            AuthResult(error = Error.AUTH_FAILED)
        }

    }

    suspend fun register(userCredentials: UserCredentials): AuthResult {
        return try {
            val result = caffShopApi.register(userCredentials)
            return if (result.isSuccessful && result.body() != null) {
                // TODO
                AuthResult(success = User(userCredentials.username, ""))
            } else if (result.errorBody() != null) {
                // TODO send specific error based on the response error code
                AuthResult(error = Error.AUTH_FAILED)
            } else {
                AuthResult(error = Error.AUTH_FAILED)
            }
        } catch (e: Exception) {
            AuthResult(error = Error.AUTH_FAILED)
        }
    }

    suspend fun getCaffs(token: String) {
        return try {
            val result = caffShopApi.getCaffs(createAuthHeaderFromToken(token))
            TODO()
        } catch (e: Exception) {
            TODO()
        }
    }

    private fun createAuthHeaderFromToken(token: String) = "Bearer $token"

}