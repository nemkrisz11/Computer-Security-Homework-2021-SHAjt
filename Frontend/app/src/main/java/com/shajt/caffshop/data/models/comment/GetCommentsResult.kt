package com.shajt.caffshop.data.models.comment

import com.shajt.caffshop.data.enums.ErrorMessage
import com.shajt.caffshop.data.models.CommentList

/**
 * Get comment result : success (comment list) or error.
 */
data class GetCommentsResult(
    val success: CommentList? = null,
    val error: ErrorMessage? = null
)