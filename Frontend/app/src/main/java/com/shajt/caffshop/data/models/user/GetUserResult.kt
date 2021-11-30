package com.shajt.caffshop.data.models.user

import com.shajt.caffshop.data.enums.ErrorMessage
import com.shajt.caffshop.data.models.UserData

/**
 * Get user result : success (user data) or error.
 */
data class GetUserResult(
    val success: UserData? = null,
    val error: ErrorMessage? = null
)