package com.shajt.caffshop.data.models.comment

import com.shajt.caffshop.data.enums.ErrorMessage

/**
 * Post comment result : success or error.
 */
data class PostCommentResult(
    val success: Boolean = false,
    val error: ErrorMessage? = null
)