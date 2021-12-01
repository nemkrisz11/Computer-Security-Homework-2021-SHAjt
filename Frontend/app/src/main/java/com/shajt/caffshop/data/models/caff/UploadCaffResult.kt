package com.shajt.caffshop.data.models.caff

import com.shajt.caffshop.data.enums.ErrorMessage

/**
 * Caff upload result : success or error.
 */
data class UploadCaffResult(
    val success: Boolean = false,
    val error: ErrorMessage? = null
)