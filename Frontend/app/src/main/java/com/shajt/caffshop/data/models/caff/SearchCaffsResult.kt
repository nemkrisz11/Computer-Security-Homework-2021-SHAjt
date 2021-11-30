package com.shajt.caffshop.data.models.caff

import com.shajt.caffshop.data.enums.ErrorMessage
import com.shajt.caffshop.data.models.CaffList

/**
 * Caff search result : success (caff list) or error.
 */
data class SearchCaffsResult(
    val success: CaffList? = null,
    val error: ErrorMessage? = null
)