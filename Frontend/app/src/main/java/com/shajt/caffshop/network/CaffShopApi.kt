package com.shajt.caffshop.network

import com.shajt.caffshop.data.models.*
import com.shajt.caffshop.data.models.auth.LoginResult
import com.shajt.caffshop.data.models.auth.UserCredentials
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface CaffShopApi {

    companion object {
        const val BASE_URL = "http://localhost:8080/"
    }

    @GET("/user")
    suspend fun getUsers(
        @Header("Authorization") authHeader: String,
        @Query("page") page: Int,
        @Query("perpage") perpage: Int
    ): Response<UserList>

    @GET("/user/{username}")
    suspend fun getUser(
        @Header("Authorization") authHeader: String,
        @Path("username") username: String
    ): Response<UserData>

    @DELETE("/user/{username}")
    suspend fun deleteUser(
        @Header("Authorization") authHeader: String,
        @Path("username") username: String
    )

    @POST("/user/register")
    suspend fun register(
        @Body userCredentials: UserCredentials
    )

    @POST("/user/login")
    suspend fun login(
        @Body userCredentials: UserCredentials
    ): Response<LoginResult>

    @POST("/user/logout")
    suspend fun logout(
        @Header("Authorization") authHeader: String,
    )

    @POST("/user/password")
    suspend fun modifyPassword(
        @Header("Authorization") authHeader: String,
        @Body modifyPassword: ModifyPassword
    )

    @GET("/caff/{id}")
    suspend fun getCaff(
        @Header("Authorization") authHeader: String,
        @Path("id") id: String
    ): Response<Caff>

    @DELETE("/caff/{id}")
    suspend fun deleteCaff(
        @Header("Authorization") authHeader: String,
        @Path("id") id: String
    )

    @GET("/caff/search")
    suspend fun searchCaffs(
        @Header("Authorization") authHeader: String,
        @Query("searchTerm") searchTerm: String? = null,
        @Query("username") username: String? = null,
        @Query("uploaderName") uploaderName: String? = null,
        @Query("creationDate") creationDate: Long? = null,
        @Query("uploadDate") uploadDate: Long? = null,
        @Query("page") page: Int,
        @Query("perpage") perpage: Int
    ): Response<CaffList>

    @Multipart
    @POST("/caff/upload")
    suspend fun uploadCaff(
        @Header("Authorization") authHeader: String,
        @Part("name") name: String,
        @Part file: MultipartBody.Part
    )

    @Streaming
    @GET("/caff/download/{id}")
    suspend fun downloadCaff(
        @Header("Authorization") authHeader: String,
        @Path("id") id: String
    ): Response<ResponseBody>

    @GET("/comment")
    suspend fun getComments(
        @Header("Authorization") authHeader: String,
        @Query("caffId") caffId: String,
        @Query("page") page: Int,
        @Query("perpage") perpage: Int
    ): Response<CommentList>

    @POST("/comment")
    suspend fun postComment(
        @Header("Authorization") authHeader: String,
        @Body commentToCreate: CommentToCreate
    )

    @DELETE("/comment")
    suspend fun deleteComment(
        @Header("Authorization") authHeader: String,
        @Query("commentId") commentId: Int,
        @Query("caffId") caffId: String
    )
}