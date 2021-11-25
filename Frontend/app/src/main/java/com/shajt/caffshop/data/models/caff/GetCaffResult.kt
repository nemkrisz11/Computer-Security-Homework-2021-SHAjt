package com.shajt.caffshop.data.models.caff

import com.shajt.caffshop.data.enums.ErrorMessage
import com.shajt.caffshop.data.models.Caff

data class GetCaffResult(
    val success: Caff? = null,
    val error: ErrorMessage? = null
)