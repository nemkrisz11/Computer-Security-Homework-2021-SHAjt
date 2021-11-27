package com.shajt.caffshop.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import androidx.core.content.edit
import com.shajt.caffshop.data.enums.ErrorMessage
import com.shajt.caffshop.data.models.*
import com.shajt.caffshop.data.models.auth.AuthResult
import com.shajt.caffshop.data.models.auth.LoginResult
import com.shajt.caffshop.data.models.auth.UserCredentials
import com.shajt.caffshop.data.models.caff.DeleteCaffResult
import com.shajt.caffshop.data.models.caff.GetCaffResult
import com.shajt.caffshop.data.models.caff.SearchCaffsResult
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
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CaffShopRepository @Inject constructor(
    private val apiInteractor: CaffShopApiInteractor,
    @ApplicationContext baseContext: Context
) {

    companion object {
        const val ENCRYPTION_ALIAS = "caffShopUserToken"
    }

    private val enCryptor = EnCryptor()
    private val deCryptor = DeCryptor()

    private val sharedPreferences: SharedPreferences =
        baseContext.getSharedPreferences("caffShopPrefs", Context.MODE_PRIVATE)

    var localUser: User? = null
        private set

    val isLoggedIn: Boolean
        get() = localUser != null


    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        localUser = loadLocalUser()
    }

    suspend fun login(userCredentials: UserCredentials): AuthResult {
        if (!validateCredentials(userCredentials)) {
            return AuthResult(error = ErrorMessage.REQUIRED_FIELD_IS_EMPTY)
        }

        val loginServerResult = apiInteractor.login(userCredentials)
        var check = checkServerResult(loginServerResult, ErrorMessage.LOGIN_FAILED)
        if (check != null) {
            return AuthResult(error = check)
        }
        val loginResult = loginServerResult.result!!

        val userDataServerResult =
            apiInteractor.getUser(loginResult.token, userCredentials.username)
        check = checkServerResult(userDataServerResult, ErrorMessage.LOGIN_FAILED)
        if (check != null) {
            return AuthResult(error = check)
        }
        val userData = userDataServerResult.result!!

        /* // For testing
        val userData = UserData(userCredentials.username, false, 1000)
        val loginResult = LoginResult("abcd", 1000)
         */

        val user = User(
            userData.username, loginResult.token, loginResult.expire, userData.regDate, userData.isAdmin
        )
        setLocalUser(user)
        return AuthResult(success = user)
    }

    suspend fun register(userCredentials: UserCredentials): AuthResult {
        if (!validateCredentials(userCredentials)) {
            return AuthResult(error = ErrorMessage.REQUIRED_FIELD_IS_EMPTY)
        }

        val registrationResult = apiInteractor.register(userCredentials)
        val check = checkServerResult(registrationResult, ErrorMessage.REGISTRATION_FAILED)
        if (check != null) {
            return AuthResult(error = check)
        }

        return login(userCredentials)
    }

    private fun validateCredentials(cred: UserCredentials): Boolean {
        return !(cred.username.isBlank() || cred.password.isBlank())
    }

    private fun loadLocalUser(): User? {
        with(sharedPreferences) {
            val username = getString("username", null)
            val encryptedTokenText = getString("encryptedToken", null)
            val expire = getLong("expire", -1L)
            val regDate = getLong("regDate", -1L)
            val isAdmin = getBoolean("isAdmin", false)
            val ivText = getString("iv", null)

            if (username == null || encryptedTokenText == null || expire == -1L || regDate == -1L || ivText == null) {
                return null
            }

            val encryptedToken = Base64.decode(encryptedTokenText, Base64.DEFAULT)
            val iv = Base64.decode(ivText, Base64.DEFAULT)
            val token = deCryptor.decryptData(ENCRYPTION_ALIAS, encryptedToken, iv)

            return User(username, token, expire, regDate, isAdmin)
        }
    }

    private fun setLocalUser(user: User) {
        this.localUser = user
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore

        val encryptedToken = enCryptor.encryptText(ENCRYPTION_ALIAS, user.token)
        val encryptedTokenText = Base64.encodeToString(encryptedToken, Base64.DEFAULT)
        val ivText = Base64.encodeToString(enCryptor.iv, Base64.DEFAULT)

        sharedPreferences.edit(commit = true) {
            putString("username", user.username)
            putString("encryptedToken", encryptedTokenText)
            putLong("expire", user.expire)
            putLong("regDate", user.regDate)
            putBoolean("isAdmin", user.isAdmin)
            putString("iv", ivText)
        }
    }

    suspend fun logout() {
        if (localUser == null) {
            return
        }
        apiInteractor.logout(localUser!!.token)
        localUser = null
        sharedPreferences.edit(commit = true) {
            clear()
        }
    }

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

    suspend fun getCaff(caffId: Int): GetCaffResult {
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

    suspend fun deleteCaff(caffId: Int): DeleteCaffResult {
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

    suspend fun searchCaffs(
        searchTerm: String? = null,
        username: String? = null,
        uploaderName: String? = null,
        creationDate: Long? = null,
        uploadDate: Long? = null,
        page: Int = 1,
        perpage: Int = 20
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

    suspend fun uploadCaff() {

    }

    suspend fun downloadCaff() {

    }

    suspend fun getComments(caffId: Int, page: Int = 1, perpage: Int = 20): GetCommentsResult {
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

    suspend fun deleteComment(commentId: Int): DeleteCommentResult {
        if (localUser == null) {
            return DeleteCommentResult(error = ErrorMessage.INVALID_USER_DATA)
        }
        val result = apiInteractor.deleteComment(localUser!!.token, commentId)
        val check = checkServerResult(result, ErrorMessage.COMMENT_DELETE_FAILED)
        if (check != null) {
            return DeleteCommentResult(error = check)
        }
        return DeleteCommentResult(success = true)
    }

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