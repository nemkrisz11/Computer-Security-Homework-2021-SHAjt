package com.shajt.caffshop.data.models.user

import com.shajt.caffshop.data.enums.ErrorMessage
import com.shajt.caffshop.data.models.UserList

data class GetUsersResult(
    val success: UserList? = null,
    val error: ErrorMessage? = null
)