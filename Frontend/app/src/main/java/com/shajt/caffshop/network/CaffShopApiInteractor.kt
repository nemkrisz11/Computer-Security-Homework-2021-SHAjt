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
import okhttp3.Interceptor
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
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.inject.Singleton
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

@Singleton
class CaffShopApiInteractor(
    private val baseUrl: HttpUrl = CaffShopApi.BASE_URL.toHttpUrl()
) {

    private val caffShopApi: CaffShopApi

    init {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val httpClient =
            getUnsafeOkHttpClient(logging)
                ?: OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build()


        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()

        this.caffShopApi = retrofit.create(CaffShopApi::class.java)
    }

    // Should be removed completely if the server is signed with a non self signed certificate
    private fun getUnsafeOkHttpClient(interceptor: Interceptor): OkHttpClient? {
        return try {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts = arrayOf<TrustManager>(
                object : X509TrustManager {

                    @Throws(CertificateException::class)
                    override fun checkClientTrusted(
                        chain: Array<X509Certificate?>?,
                        authType: String?
                    ) {
                    }

                    @Throws(CertificateException::class)
                    override fun checkServerTrusted(
                        chain: Array<X509Certificate?>?,
                        authType: String?
                    ) {
                    }

                    override fun getAcceptedIssuers(): Array<X509Certificate?>? {
                        return arrayOf()
                    }
                }
            )

            // Install the all-trusting trust manager
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())
            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory = sslContext.socketFactory
            val trustManagerFactory: TrustManagerFactory =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            trustManagerFactory.init(null as KeyStore?)
            val trustManagers: Array<TrustManager> =
                trustManagerFactory.trustManagers
            check(!(trustManagers.size != 1 || trustManagers[0] !is X509TrustManager)) {
                "Unexpected default trust managers:" + trustManagers.contentToString()
            }

            val trustManager =
                trustManagers[0] as X509TrustManager


            val builder = OkHttpClient.Builder()
            builder.addInterceptor(interceptor)
            builder.sslSocketFactory(sslSocketFactory, trustManager)
            builder.hostnameVerifier(HostnameVerifier { _, _ -> true })
            builder.build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }


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
            checkResponse(response, ErrorMessage.LOGIN_FAILED)
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
        id: String
    ): ServerResult<Caff, ErrorMessage> {
        return try {
            val response = caffShopApi.getCaff(createAuthHeaderFromToken(token), id)
            checkResponse(response, ErrorMessage.CAFF_REQUEST_FAILED)
        } catch (e: Exception) {
            ServerResult(error = ErrorMessage.CAFF_REQUEST_FAILED)
        }
    }

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
        id: String
    ): ServerResult<CaffRaw, ErrorMessage> {
        return try {
            val response = caffShopApi.downloadCaff(createAuthHeaderFromToken(token), id)
            checkResponse(response, ErrorMessage.CAFF_DOWNLOAD_FAILED)
        } catch (e: Exception) {
            ServerResult(error = ErrorMessage.CAFF_DOWNLOAD_FAILED)
        }
    }

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

    private fun createAuthHeaderFromToken(token: String) = "Bearer $token"

    private fun <R> checkResponse(
        response: Response<R>,
        defaultErrorMessage: ErrorMessage = ErrorMessage.NETWORK_ERROR
    ): ServerResult<R, ErrorMessage> {
        return if (response.isSuccessful && response.body() != null) {
            ServerResult(result = response.body()!!)
        } else {
            createErrorMessage(response.errorBody().toString(), defaultErrorMessage)
        }
    }

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