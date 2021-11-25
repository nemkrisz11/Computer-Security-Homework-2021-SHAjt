package com.shajt.caffshop.data.models.caff

import com.shajt.caffshop.data.enums.ErrorMessage

data class DeleteCaffResult(
    val success: Boolean = false,
    val error: ErrorMessage? = null
)