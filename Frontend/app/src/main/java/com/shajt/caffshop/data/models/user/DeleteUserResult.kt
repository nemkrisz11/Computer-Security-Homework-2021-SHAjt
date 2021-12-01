package com.shajt.caffshop.data.models.user

import com.shajt.caffshop.data.enums.ErrorMessage

/**
 * Delete user result : success or error.
 */
data class DeleteUserResult(
    val success: Boolean = false,
    val error: ErrorMessage? = null
)