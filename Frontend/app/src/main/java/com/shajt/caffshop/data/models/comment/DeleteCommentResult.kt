package com.shajt.caffshop.data.models.comment

import com.shajt.caffshop.data.enums.ErrorMessage

/**
 * Comment delete result : success or error.
 */
data class DeleteCommentResult(
    val success: Boolean = false,
    val error: ErrorMessage? = null
)