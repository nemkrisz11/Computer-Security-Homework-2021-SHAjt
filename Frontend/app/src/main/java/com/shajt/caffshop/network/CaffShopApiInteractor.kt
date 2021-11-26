package com.shajt.caffshop.network

import com.google.gson.Gson
import com.shajt.caffshop.data.enums.ErrorMessage
import com.shajt.caffshop.data.models.*
import com.shajt.caffshop.data.models.auth.LoginResult
import com.shajt.caffshop.data.models.auth.UserCredentials
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import javax.inject.Singleton
import kotlin.Error

@Singleton
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


    suspend fun getUsers(
        token: String,
        page: Int = 1,
        perpage: Int = 20
    ): ServerResult<UserList, ErrorMessage> {
        return try {
            val response = caffShopApi.getUsers(createAuthHeaderFromToken(token), page, perpage)
            checkResponse(response)
        } catch (e: Exception) {
            ServerResult(error = ErrorMessage.USER_LIST_REQUEST_FAILED)
        }
    }

    suspend fun getUser(
        token: String,
        username: String
    ): ServerResult<UserData, ErrorMessage> {
        return try {
            val response = caffShopApi.getUser(createAuthHeaderFromToken(token), username)
            checkResponse(response)
        } catch (e: Exception) {
            ServerResult(error = ErrorMessage.USER_REQUEST_FAILED)
        }
    }

    suspend fun deleteUser(
        token: String,
        username: String
    ): ServerResult<Boolean, ErrorMessage> {
        return try {
            caffShopApi.deleteUser(createAuthHeaderFromToken(token), username)
            ServerResult(result = true)
        } catch (e: HttpException) {
            createErrorMessage(
                e.response()?.errorBody()?.charStream()?.readText(),
                ErrorMessage.USER_DELETE_FAILED
            )
        } catch (e: Exception) {
            ServerResult(error = ErrorMessage.USER_DELETE_FAILED)
        }
    }

    suspend fun register(
        userCredentials: UserCredentials
    ): ServerResult<Boolean, ErrorMessage> {
        return try {
            caffShopApi.register(userCredentials)
            ServerResult(result = true)
        } catch (e: HttpException) {
            createErrorMessage(
                e.response()?.errorBody()?.charStream()?.readText(),
                ErrorMessage.REGISTRATION_FAILED
            )
        } catch (e: Exception) {
            ServerResult(error = ErrorMessage.REGISTRATION_FAILED)
        }
    }

    suspend fun login(
        userCredentials: UserCredentials
    ): ServerResult<LoginResult, ErrorMessage> {
        return try {
            val response = caffShopApi.login(userCredentials)
            checkResponse(response)
        } catch (e: Exception) {
            ServerResult(error = ErrorMessage.LOGIN_FAILED)
        }
    }

    suspend fun logout(
        token: String
    ): ServerResult<Boolean, ErrorMessage> {
        return try {
            caffShopApi.logout(createAuthHeaderFromToken(token))
            ServerResult(result = true)
        } catch (e: HttpException) {
            createErrorMessage(
                e.response()?.errorBody()?.charStream()?.readText(),
                ErrorMessage.LOGOUT_FAILED
            )
        } catch (e: Exception) {
            ServerResult(error = ErrorMessage.LOGOUT_FAILED)
        }
    }

    suspend fun modifyPassword(
        token: String,
        modifyPassword: ModifyPassword
    ): ServerResult<Boolean, ErrorMessage> {
        return try {
            caffShopApi.modifyPassword(createAuthHeaderFromToken(token), modifyPassword)
            ServerResult(result = true)
        } catch (e: HttpException) {
            createErrorMessage(
                e.response()?.errorBody()?.charStream()?.readText(),
                ErrorMessage.MODIFY_PASSWORD_FAILED
            )
        } catch (e: Exception) {
            ServerResult(error = ErrorMessage.MODIFY_PASSWORD_FAILED)
        }
    }

    suspend fun getCaff(
        token: String,
        id: Int
    ): ServerResult<Caff, ErrorMessage> {
        return try {
            val response = caffShopApi.getCaff(createAuthHeaderFromToken(token), id)
            checkResponse(response)
        } catch (e: Exception) {
            ServerResult(error = ErrorMessage.CAFF_REQUEST_FAILED)
        }
    }

    suspend fun deleteCaff(
        token: String,
        id: Int
    ): ServerResult<Boolean, ErrorMessage> {
        return try {
            caffShopApi.deleteCaff(createAuthHeaderFromToken(token), id)
            ServerResult(result = true)
        } catch (e: HttpException) {
            createErrorMessage(
                e.response()?.errorBody()?.charStream()?.readText(),
                ErrorMessage.CAFF_DELETE_FAILED
            )
        } catch (e: Exception) {
            ServerResult(error = ErrorMessage.CAFF_DELETE_FAILED)
        }
    }

    suspend fun searchCaffs(
        token: String,
        searchTerm: String? = null,
        username: String? = null,
        uploaderName: String? = null,
        creationDate: Long? = null,
        uploadDate: Long? = null,
        page: Int = 1,
        perpage: Int = 20
    ): ServerResult<CaffList, ErrorMessage> {
        return try {
            val response = caffShopApi.searchCaffs(
                createAuthHeaderFromToken(token),
                searchTerm,
                username,
                uploaderName,
                creationDate,
                uploadDate,
                page,
                perpage
            )
            checkResponse(response)
        } catch (e: Exception) {
            ServerResult(error = ErrorMessage.CAFF_SEARCH_FAILED)
        }
    }

    suspend fun uploadCaff(
        token: String,
        name: String,
        file: File
    ): ServerResult<Boolean, ErrorMessage> {
        return try {
            val requestBody = file.asRequestBody("multipart/form-data".toMediaType())
            val multiPartBody = MultipartBody.Part.createFormData("file", file.name, requestBody)
            caffShopApi.uploadCaff(createAuthHeaderFromToken(token), name, multiPartBody)
            ServerResult(result = true)
        } catch (e: HttpException) {
            createErrorMessage(
                e.response()?.errorBody()?.charStream()?.readText(),
                ErrorMessage.CAFF_UPLOAD_FAILED
            )
        } catch (e: Exception) {
            ServerResult(error = ErrorMessage.CAFF_UPLOAD_FAILED)
        }
    }

    suspend fun downloadCaff(
        token: String,
        id: Int
    ): ServerResult<CaffRaw, ErrorMessage> {
        return try {
            val response = caffShopApi.downloadCaff(createAuthHeaderFromToken(token), id)
            checkResponse(response)
        } catch (e: Exception) {
            ServerResult(error = ErrorMessage.CAFF_DOWNLOAD_FAILED)
        }
    }

    suspend fun getComments(
        token: String,
        caffId: Int,
        page: Int = 1,
        perpage: Int = 20,
    ): ServerResult<CommentList, ErrorMessage> {
        return try {
            val response = caffShopApi.getComments(createAuthHeaderFromToken(token), caffId, page, perpage)
            checkResponse(response)
        } catch (e: Exception) {
            ServerResult(error = ErrorMessage.COMMENT_LIST_REQUEST_FAILED)
        }
    }

    suspend fun postComment(
        token: String,
        commentToCreate: CommentToCreate
    ): ServerResult<Boolean, ErrorMessage> {
        return try {
            caffShopApi.postComment(createAuthHeaderFromToken(token), commentToCreate)
            ServerResult(result = true)
        } catch (e: HttpException) {
            createErrorMessage(
                e.response()?.errorBody()?.charStream()?.readText(),
                ErrorMessage.COMMENT_POST_FAILED
            )
        } catch (e: Exception) {
            ServerResult(error = ErrorMessage.COMMENT_POST_FAILED)
        }
    }

    suspend fun deleteComment(
        token: String,
        id: Int
    ): ServerResult<Boolean, ErrorMessage> {
        return try {
            caffShopApi.deleteComment(createAuthHeaderFromToken(token), id)
            ServerResult(result = true)
        } catch (e: HttpException) {
            createErrorMessage(
                e.response()?.errorBody()?.charStream()?.readText(),
                ErrorMessage.COMMENT_DELETE_FAILED
            )
        } catch (e: Exception) {
            ServerResult(error = ErrorMessage.COMMENT_DELETE_FAILED)
        }
    }

    private fun createAuthHeaderFromToken(token: String) = "Bearer $token"

    private fun <R> checkResponse(
        response: Response<R>
    ): ServerResult<R, ErrorMessage> {
        return if (response.isSuccessful && response.body() != null) {
            ServerResult(result = response.body()!!)
        } else {
            createErrorMessage(response.errorBody().toString())
        }
    }

    private fun <R> createErrorMessage(
        errorBody: String?,
        defaultErrorMessage: ErrorMessage = ErrorMessage.NETWORK_ERROR
    ): ServerResult<R, ErrorMessage> {
        if (errorBody.isNullOrBlank()) {
            return ServerResult(error = defaultErrorMessage)
        }
        val error = Gson().fromJson(errorBody, Error::class.java)
        // TODO specify error

        return ServerResult(error = defaultErrorMessage)
    }

}