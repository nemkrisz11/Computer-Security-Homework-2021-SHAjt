package com.shajt.caffshop.network

import com.google.gson.JsonObject
import com.shajt.caffshop.data.models.auth.UserCredentials
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface CaffShopApi {

    companion object {
        const val BASE_URL = "http://localhost:8080/"
    }

    @POST("/login")
    suspend fun login(
        @Body userCredentials: UserCredentials
    ): Response<JsonObject>

    @POST("/register")
    suspend fun register(
        @Body userCredentials: UserCredentials
    ): Response<JsonObject>

    @GET("/caff")
    suspend fun getCaffs(
        @Header("Authorization") authHeader: String,
    )
}