package com.shajt.caffshop.data.models.user

import com.shajt.caffshop.data.enums.ErrorMessage
import com.shajt.caffshop.data.models.UserList

/**
 * Get users result : success (user list) or error.
 */
data class GetUsersResult(
    val success: UserList? = null,
    val error: ErrorMessage? = null
)