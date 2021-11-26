package com.shajt.caffshop.data.models.user

import com.shajt.caffshop.data.enums.ErrorMessage

data class ModifyPasswordResult (
    val success: Boolean = false,
    val error: ErrorMessage? = null
)