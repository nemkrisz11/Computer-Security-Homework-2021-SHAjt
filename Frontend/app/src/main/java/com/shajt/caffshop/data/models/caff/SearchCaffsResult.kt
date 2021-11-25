package com.shajt.caffshop.data.models.caff

import com.shajt.caffshop.data.enums.ErrorMessage
import com.shajt.caffshop.data.models.CaffList

data class SearchCaffsResult(
    val success: CaffList? = null,
    val error: ErrorMessage? = null
)