package com.shajt.caffshop.data.models.caff

import com.shajt.caffshop.data.enums.ErrorMessage

data class DownloadCaffResult(
    val success: String? = null,
    val error: ErrorMessage? = null
)