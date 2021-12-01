package com.shajt.caffshop.data.models.user

import com.shajt.caffshop.data.enums.ErrorMessage

/**
 * Modify password result : success or error.
 */
data class ModifyPasswordResult (
    val success: Boolean = false,
    val error: ErrorMessage? = null
)