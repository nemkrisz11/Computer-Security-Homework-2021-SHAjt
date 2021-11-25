package com.shajt.caffshop.data.models.user

import com.shajt.caffshop.data.enums.ErrorMessage
import com.shajt.caffshop.data.models.UserData

data class GetUserResult(
    val success: UserData? = null,
    val error: ErrorMessage? = null
)