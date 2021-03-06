package com.shajt.caffshop.network

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
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
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import javax.net.ssl.HostnameVerifier

@Singleton
class CaffShopApiInteractor(
    private val baseUrl: HttpUrl = CaffShopApi.BASE_URL.toHttpUrl()
) {

    private val caffShopApi: CaffShopApi

    init {
        // Creating logger
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.HEADERS
        }

        // Creating http client
        val httpClient = OkHttpClient.Builder()
            .sslSocketFactory(CustomTrust.certificate.sslSocketFactory(), CustomTrust.certificate.trustManager)
            .hostnameVerifier(HostnameVerifier { hostname, session ->
                session.isValid
            })
            .addInterceptor(logging)
            .callTimeout(1, TimeUnit.MINUTES)
            .build()

        // Creating retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()

        this.caffShopApi = retrofit.create(CaffShopApi::class.java)
    }

    /**
     * Requests for user list.
     */
    suspend fun getUsers(
        token: String,
        page: Int = 1,
        perpage: Int = 20
    ): ServerResult<UserList, ErrorMessage> {
        return try {
            val response = caffShopApi.getUsers(createAuthHeaderFromToken(token), page, perpage)
            checkResponse(response, ErrorMessage.USER_LIST_REQUEST_FAILED)
        } catch (e: Exception) {
            ServerResult(error = ErrorMessage.USER_LIST_REQUEST_FAILED)
        }
    }

    /**
     * Requests for user data.
     */
    suspend fun getUser(
        token: String,
        username: String
    ): ServerResult<UserData, ErrorMessage> {
        return try {
            val response = caffShopApi.getUser(createAuthHeaderFromToken(token), username)
            checkResponse(response, ErrorMessage.USER_REQUEST_FAILED)
        } catch (e: Exception) {
            ServerResult(error = ErrorMessage.USER_REQUEST_FAILED)
        }
    }

    /**
     * Deletes user.
     */
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

    /**
     * Registers a user.
     */
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

    /**
     * Logs in a user.
     */
    suspend fun login(
        userCredentials: UserCredentials
    ): ServerResult<LoginResult, ErrorMessage> {
        return try {
            val response = caffShopApi.login(userCredentials)
            checkResponse(response, ErrorMessage.LOGIN_FAILED)
        } catch (e: Exception) {
            ServerResult(error = ErrorMessage.LOGIN_FAILED)
        }
    }

    /**
     * Logs out a user.
     */
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

    /**
     * Modifies user password.
     */
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

    /**
     * Requests for caff data.
     */
    suspend fun getCaff(
        token: String,
        id: String
    ): ServerResult<Caff, ErrorMessage> {
        return try {
            val response = caffShopApi.getCaff(createAuthHeaderFromToken(token), id)
            checkResponse(response, ErrorMessage.CAFF_REQUEST_FAILED)
        } catch (e: Exception) {
            ServerResult(error = ErrorMessage.CAFF_REQUEST_FAILED)
        }
    }

    /**
     * Deletes a caff.
     */
    suspend fun deleteCaff(
        token: String,
        id: String
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

    /**
     * Searches caffs.
     */
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
            checkResponse(response, ErrorMessage.CAFF_SEARCH_FAILED)
        } catch (e: Exception) {
            ServerResult(error = ErrorMessage.CAFF_SEARCH_FAILED)
        }
    }

    /**
     * Uploads caff.
     */
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

    /**
     * Downloads caff.
     */
    suspend fun downloadCaff(
        token: String,
        id: String
    ): ServerResult<ResponseBody, ErrorMessage> {
        return try {
            val response = caffShopApi.downloadCaff(createAuthHeaderFromToken(token), id)
            checkResponse(response, ErrorMessage.CAFF_DOWNLOAD_FAILED)
        } catch (e: Exception) {
            ServerResult(error = ErrorMessage.CAFF_DOWNLOAD_FAILED)
        }
    }

    /**
     * Enques a download.
     */
    suspend fun enqueueDownload(
        token: String,
        id: String,
        context: Context
    ): Long {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val fullPath = "${baseUrl}caff/download/${id}"
        val request = DownloadManager.Request(Uri.parse(fullPath)).apply {
            addRequestHeader("Authorization", createAuthHeaderFromToken(token))
        }
        return downloadManager.enqueue(request)
    }

    /**
     * Requests for comment list.
     */
    suspend fun getComments(
        token: String,
        caffId: String,
        page: Int = 1,
        perpage: Int = 20,
    ): ServerResult<CommentList, ErrorMessage> {
        return try {
            val response =
                caffShopApi.getComments(createAuthHeaderFromToken(token), caffId, page, perpage)
            checkResponse(response, ErrorMessage.COMMENT_LIST_REQUEST_FAILED)
        } catch (e: Exception) {
            ServerResult(error = ErrorMessage.COMMENT_LIST_REQUEST_FAILED)
        }
    }

    /**
     * Posts comment.
     */
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

    /**
     * Deletes comment.
     */
    suspend fun deleteComment(
        token: String,
        commentId: Int,
        caffId: String
    ): ServerResult<Boolean, ErrorMessage> {
        return try {
            caffShopApi.deleteComment(createAuthHeaderFromToken(token), commentId, caffId)
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

    /**
     * Creates auth header from token.
     */
    private fun createAuthHeaderFromToken(token: String) = "Bearer $token"

    /**
     * Checks response.
     */
    private fun <R> checkResponse(
        response: Response<R>,
        defaultErrorMessage: ErrorMessage = ErrorMessage.NETWORK_ERROR
    ): ServerResult<R, ErrorMessage> {
        return if (response.isSuccessful && response.body() != null) {
            ServerResult(result = response.body()!!)
        } else {
            createErrorMessage(response.errorBody()?.charStream()?.readText(), defaultErrorMessage)
        }
    }

    /**
     * Creates error message.
     */
    private fun <R> createErrorMessage(
        errorBody: String?,
        defaultErrorMessage: ErrorMessage = ErrorMessage.NETWORK_ERROR
    ): ServerResult<R, ErrorMessage> {
        if (errorBody.isNullOrBlank()) {
            return ServerResult(error = defaultErrorMessage)
        }

        var error = defaultErrorMessage
        try {
            val serverError = Gson().fromJson(errorBody, Error::class.java)
            error = ServerErrorMapper.mapServerError(serverError.errorId, defaultErrorMessage)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ServerResult(error = error)
    }

}