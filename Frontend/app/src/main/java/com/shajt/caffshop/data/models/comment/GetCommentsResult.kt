package com.shajt.caffshop.data.models.comment

import com.shajt.caffshop.data.enums.ErrorMessage
import com.shajt.caffshop.data.models.CommentList

data class GetCommentsResult(
    val success: CommentList? = null,
    val error: ErrorMessage? = null
)