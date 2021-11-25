package com.shajt.caffshop.data.models.comment

import com.shajt.caffshop.data.enums.ErrorMessage

data class DeleteCommentResult(
    val success: Boolean = false,
    val error: ErrorMessage? = null
)