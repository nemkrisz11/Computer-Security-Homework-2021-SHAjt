package com.shajt.caffshop.data

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Environment
import android.util.Base64
import androidx.core.content.edit
import com.shajt.caffshop.data.enums.ErrorMessage
import com.shajt.caffshop.data.models.*
import com.shajt.caffshop.data.models.auth.AuthResult
import com.shajt.caffshop.data.models.auth.UserCredentials
import com.shajt.caffshop.data.models.caff.*
import com.shajt.caffshop.data.models.comment.DeleteCommentResult
import com.shajt.caffshop.data.models.comment.GetCommentsResult
import com.shajt.caffshop.data.models.comment.PostCommentResult
import com.shajt.caffshop.data.models.user.DeleteUserResult
import com.shajt.caffshop.data.models.user.GetUserResult
import com.shajt.caffshop.data.models.user.GetUsersResult
import com.shajt.caffshop.data.models.user.ModifyPasswordResult
import com.shajt.caffshop.network.CaffShopApiInteractor
import com.shajt.caffshop.utils.DeCryptor
import com.shajt.caffshop.utils.EnCryptor
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Caff Shop repository. Builds connection between network and UI layer.
 */
@Singleton
class CaffShopRepository @Inject constructor(
    private val apiInteractor: CaffShopApiInteractor,
    @ApplicationContext baseContext: Context
) {

    companion object {
        const val ENCRYPTION_ALIAS = "caffShopUserToken"
    }

    // For data encryption and decryption
    private val enCryptor = EnCryptor()
    private val deCryptor = DeCryptor()

    private val sharedPreferences: SharedPreferences =
        baseContext.getSharedPreferences("caffShopPrefs", Context.MODE_PRIVATE)

    var localUser: User? = null
        private set

    val isLoggedIn: Boolean
        get() = localUser != null


    init {
        localUser = loadLocalUser()
    }

    /**
     * Logs in a user.
     *
     * @param userCredentials credentials of the user
     * @return  authentication result
     */
    suspend fun login(userCredentials: UserCredentials): AuthResult {
        if (!validateCredentials(userCredentials)) {
            return AuthResult(error = ErrorMessage.REQUIRED_FIELD_IS_EMPTY)
        }

        // Start login operation
        val loginServerResult = apiInteractor.login(userCredentials)
        var check = checkServerResult(loginServerResult, ErrorMessage.LOGIN_FAILED)
        if (check != null) {
            return AuthResult(error = check)
        }
        val loginResult = loginServerResult.result!!

        // Call for user data
        val userDataServerResult = apiInteractor.getUser(loginResult.token, userCredentials.username)
        check = checkServerResult(userDataServerResult, ErrorMessage.LOGIN_FAILED)
        if (check != null) {
            return AuthResult(error = check)
        }
        val userData = userDataServerResult.result!!

        val user = User(
            userData.username, loginResult.token, loginResult.expire, userData.regDate, userData.isAdmin
        )
        // Saving user locally
        setLocalUser(user)
        return AuthResult(success = user)
    }

    /**
     * Registers and logs in a new user.
     *
     * @param userCredentials credentials of the user
     * @return  authentication result
     */
    suspend fun register(userCredentials: UserCredentials): AuthResult {
        if (!validateCredentials(userCredentials)) {
            return AuthResult(error = ErrorMessage.REQUIRED_FIELD_IS_EMPTY)
        }

        // Starting register operation
        val registrationResult = apiInteractor.register(userCredentials)
        val check = checkServerResult(registrationResult, ErrorMessage.REGISTRATION_FAILED)
        if (check != null) {
            return AuthResult(error = check)
        }
        // Logging in user
        return login(userCredentials)
    }

    /**
     * Basic credential validation.
     *
     * @param cred credentials of the user
     * @return true if valid else false
     */
    private fun validateCredentials(cred: UserCredentials): Boolean {
        return !(cred.username.isBlank() || cred.password.isBlank())
    }

    /**
     * Loads locally saved user data.
     *
     * @return user if local user data available and valid else null
     */
    private fun loadLocalUser(): User? {
        with(sharedPreferences) {
            val username = getString("username", null)
            val encryptedTokenText = getString("encryptedToken", null)
            val expire = getLong("expire", -1L)
            val regDate = getLong("regDate", -1L)
            val isAdmin = getBoolean("isAdmin", false)
            val ivText = getString("iv", null)

            // Validating user data
            if (username == null || encryptedTokenText == null || expire == -1L ||
                expire - System.currentTimeMillis() < 1_000_000 || regDate == -1L ||
                ivText == null
            ) {
                return null
            }

            // Decrypting user token
            val encryptedToken = Base64.decode(encryptedTokenText, Base64.DEFAULT)
            val iv = Base64.decode(ivText, Base64.DEFAULT)
            val token = deCryptor.decryptData(ENCRYPTION_ALIAS, encryptedToken, iv)

            return User(username, token, expire, regDate, isAdmin)
        }
    }

    /**
     * Sets a locally saved user.
     *
     * @param user user to set
     */
    private fun setLocalUser(user: User) {
        this.localUser = user

        // Encrypting user token
        val encryptedToken = enCryptor.encryptText(ENCRYPTION_ALIAS, user.token)
        val encryptedTokenText = Base64.encodeToString(encryptedToken, Base64.DEFAULT)
        val ivText = Base64.encodeToString(enCryptor.iv, Base64.DEFAULT)

        // Saving user data
        sharedPreferences.edit(commit = true) {
            putString("username", user.username)
            putString("encryptedToken", encryptedTokenText)
            putLong("expire", user.expire)
            putLong("regDate", user.regDate)
            putBoolean("isAdmin", user.isAdmin)
            putString("iv", ivText)
        }
    }

    /**
     * Logs out a user.
     */
    suspend fun logout() {
        if (localUser == null) {
            return
        }
        apiInteractor.logout(localUser!!.token)
        localUser = null
        // Removing locally saved data.
        sharedPreferences.edit(commit = true) {
            clear()
        }
    }

    /**
     * Modifies user password.
     *
     * @param modifyPassword password modification request
     * @return password modification response
     */
    suspend fun modifyPassword(modifyPassword: ModifyPassword): ModifyPasswordResult {
        if (localUser == null) {
            return ModifyPasswordResult(error = ErrorMessage.INVALID_USER_DATA)
        }
        val result = apiInteractor.modifyPassword(localUser!!.token, modifyPassword)
        val check = checkServerResult(result, ErrorMessage.MODIFY_PASSWORD_FAILED)
        if (check != null) {
            return ModifyPasswordResult(error = check)
        }
        return ModifyPasswordResult(success = true)
    }

    /**
     * Requests for user list.
     *
     * @param page      page number
     * @param perpage   perpage number
     * @return user list or error
     */
    suspend fun getUsers(page: Int = 1, perpage: Int = 20): GetUsersResult {
        if (localUser == null) {
            return GetUsersResult(error = ErrorMessage.INVALID_USER_DATA)
        }
        val result = apiInteractor.getUsers(localUser!!.token, page, perpage)
        val check = checkServerResult(result, ErrorMessage.USER_LIST_REQUEST_FAILED)
        if (check != null) {
            return GetUsersResult(error = check)
        }
        return GetUsersResult(success = result.result)
    }

    /**
     * Requests for user data.
     *
     * @param username username
     * @return user data or error
     */
    suspend fun getUser(username: String): GetUserResult {
        if (localUser == null) {
            return GetUserResult(error = ErrorMessage.INVALID_USER_DATA)
        }
        val result = apiInteractor.getUser(localUser!!.token, username)
        val check = checkServerResult(result, ErrorMessage.USER_REQUEST_FAILED)
        if (check != null) {
            return GetUserResult(error = check)
        }
        return GetUserResult(success = result.result)
    }

    /**
     * Deletes a user.
     *
     * @param username username
     * @return success of delete operation
     */
    suspend fun deleteUser(username: String): DeleteUserResult {
        if (localUser == null) {
            return DeleteUserResult(error = ErrorMessage.INVALID_USER_DATA)
        }
        val result = apiInteractor.deleteUser(localUser!!.token, username)
        val check = checkServerResult(result, ErrorMessage.COMMENT_POST_FAILED)
        if (check != null) {
            return DeleteUserResult(error = check)
        }
        return DeleteUserResult(success = true)
    }

    /**
     * Requests for a caff.
     *
     * @param caffId id of the caff
     * @return caff data or error
     */
    suspend fun getCaff(caffId: String): GetCaffResult {
        if (localUser == null) {
            return GetCaffResult(error = ErrorMessage.INVALID_USER_DATA)
        }
        val result = apiInteractor.getCaff(localUser!!.token, caffId)
        val check = checkServerResult(result, ErrorMessage.CAFF_REQUEST_FAILED)
        if (check != null) {
            return GetCaffResult(error = check)
        }
        return GetCaffResult(success = result.result)
    }

    /**
     * Deletes a caff.
     *
     * @param caffId id of the caff
     * @return success of delete operation
     */
    suspend fun deleteCaff(caffId: String): DeleteCaffResult {
        if (localUser == null) {
            return DeleteCaffResult(error = ErrorMessage.INVALID_USER_DATA)
        }
        val result = apiInteractor.deleteCaff(localUser!!.token, caffId)
        val check = checkServerResult(result, ErrorMessage.CAFF_DELETE_FAILED)
        if (check != null) {
            return DeleteCaffResult(error = check)
        }
        return DeleteCaffResult(success = true)
    }

    /**
     * Searches caffs.
     *
     * @param searchTerm    searchTerm
     * @param username      creator name
     * @param uploaderName  uploader name
     * @param creationDate  creation date in millis
     * @param uploadDate    upload date in millis
     * @param page          page number
     * @param perpage       perpage number
     * @return found caffs or error
     */
    suspend fun searchCaffs(
        searchTerm: String? = null,
        username: String? = null,
        uploaderName: String? = null,
        creationDate: Long? = null,
        uploadDate: Long? = null,
        page: Int = 1,
        perpage: Int = 5
    ): SearchCaffsResult {
        if (localUser == null) {
            return SearchCaffsResult(error = ErrorMessage.INVALID_USER_DATA)
        }
        val result = apiInteractor.searchCaffs(
            localUser!!.token, searchTerm, username, uploaderName,
            creationDate, uploadDate, page, perpage
        )
        val check = checkServerResult(result, ErrorMessage.CAFF_SEARCH_FAILED)
        if (check != null) {
            return SearchCaffsResult(error = check)
        }
        return SearchCaffsResult(success = result.result)
    }

    /**
     * Uploads a caff.
     *
     * @param uri       uri of the file
     * @param name      name of the file
     * @param context   context
     * @return success of the upload operation
     */
    suspend fun uploadCaff(uri: Uri, name: String, context: Context): UploadCaffResult {
        if (localUser == null) {
            return UploadCaffResult(error = ErrorMessage.INVALID_USER_DATA)
        }
        // Resolving file from uri
        val descriptor = context.contentResolver.openFileDescriptor(uri, "r")
        if (descriptor == null) {
            return UploadCaffResult(error = ErrorMessage.INVALID_USER_DATA)
        }
        // Reading and creating temporary file
        val bytes = FileInputStream(descriptor.fileDescriptor).readBytes()
        val file = File(context.cacheDir, "${System.currentTimeMillis()}.caff")
        FileOutputStream(file).apply {
            write(bytes)
            flush()
            close()
        }

        val result = apiInteractor.uploadCaff(localUser!!.token, name, file)
        val check = checkServerResult(result, ErrorMessage.CAFF_UPLOAD_FAILED)
        if (check != null) {
            return UploadCaffResult(error = check)
        }
        return UploadCaffResult(success = true)
    }

    /**
     * Downloads a caff.
     *
     * @param caffId    id of the caff
     * @param fileName  name of the file
     */
    suspend fun downloadCaff(caffId: String, fileName: String): DownloadCaffResult {
        if (localUser == null) {
            return DownloadCaffResult(error = ErrorMessage.INVALID_USER_DATA)
        }
        val result = apiInteractor.downloadCaff(localUser!!.token, caffId)
        val check = checkServerResult(result, ErrorMessage.CAFF_DOWNLOAD_FAILED)
        if (check != null) {
            return DownloadCaffResult(error = check)
        }
        // Creating filename
        val fullName = "$fileName-${System.currentTimeMillis()}.caff"
        var stream: InputStream? = null
        try {
            // Getting stream
            stream = result.result!!.byteStream()
            // Getting download directory
            val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
            val file = File(path, fullName)
            val fos = FileOutputStream(file)
            fos.use { output ->
                // Reading stream and writing to file
                val buffer = ByteArray(4 * 1024)
                var read: Int
                while (stream.read(buffer).also { read = it } != -1) {
                    output.write(buffer, 0, read)
                }
                output.flush()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return DownloadCaffResult(error = ErrorMessage.CAFF_DOWNLOAD_FAILED)
        } finally {
            stream?.close()
        }
        return DownloadCaffResult(success = fullName)
    }

    /**
     * Requests for comments.
     *
     * @param caffId    id of the caff
     * @param page      page number
     * @param perpage   perpage number
     * @return comments data or error
     */
    suspend fun getComments(caffId: String, page: Int = 1, perpage: Int = 20): GetCommentsResult {
        if (localUser == null) {
            return GetCommentsResult(error = ErrorMessage.INVALID_USER_DATA)
        }
        val result = apiInteractor.getComments(localUser!!.token, caffId, page, perpage)
        val check = checkServerResult(result, ErrorMessage.COMMENT_LIST_REQUEST_FAILED)
        if (check != null) {
            return GetCommentsResult(error = check)
        }
        return GetCommentsResult(success = result.result)
    }

    /**
     * Posts a comment.
     *
     * @param commentToCreate comment to create
     * @return success of post operation
     */
    suspend fun postComment(commentToCreate: CommentToCreate): PostCommentResult {
        if (localUser == null) {
            return PostCommentResult(error = ErrorMessage.INVALID_USER_DATA)
        }
        val result = apiInteractor.postComment(localUser!!.token, commentToCreate)
        val check = checkServerResult(result, ErrorMessage.COMMENT_POST_FAILED)
        if (check != null) {
            return PostCommentResult(error = check)
        }
        return PostCommentResult(success = true)
    }

    /**
     * Deletes a comment.
     *
     * @param commentId id of the comment
     * @param caffId    id of the caff
     * @return success of delete operation
     */
    suspend fun deleteComment(commentId: Int, caffId: String): DeleteCommentResult {
        if (localUser == null) {
            return DeleteCommentResult(error = ErrorMessage.INVALID_USER_DATA)
        }
        val result = apiInteractor.deleteComment(localUser!!.token, commentId, caffId)
        val check = checkServerResult(result, ErrorMessage.COMMENT_DELETE_FAILED)
        if (check != null) {
            return DeleteCommentResult(error = check)
        }
        return DeleteCommentResult(success = true)
    }

    /**
     * Checks server result.
     *
     * @param serverResult  specific server result
     * @param baseError     specific base error type
     * @return error message if there was an error else null
     */
    private fun <R> checkServerResult(
        serverResult: ServerResult<R, ErrorMessage>,
        baseError: ErrorMessage
    ): ErrorMessage? {
        return when {
            serverResult.error != null -> serverResult.error
            serverResult.result == null -> baseError
            else -> null
        }
    }
}